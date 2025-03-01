package today.todaysentence.domain.search.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import today.todaysentence.domain.book.repository.BookRepository;
import today.todaysentence.domain.member.dto.InteractionResponseDTO;
import today.todaysentence.domain.member.service.MemberService;
import today.todaysentence.domain.post.dto.PostResponse;
import today.todaysentence.domain.post.dto.PostResponseDTO;
import today.todaysentence.domain.post.repository.PostRepositoryCustom;
import today.todaysentence.domain.search.dto.SearchResponse;
import today.todaysentence.global.exception.exception.BaseException;
import today.todaysentence.global.exception.exception.ExceptionCode;
import today.todaysentence.global.redis.RedisService;
import today.todaysentence.global.response.CommonResponse;
import today.todaysentence.global.security.userDetails.JwtUserDetails;


import java.util.*;
import java.util.stream.Collectors;

import static today.todaysentence.global.redis.RedisService.FAMOUS_RECORD_TAG_KEY;
import static today.todaysentence.global.redis.RedisService.FAMOUS_SEARCH_TAG_KEY;

@Service
@RequiredArgsConstructor
public class SearchService {

    private final BookRepository bookRepository;
    private final PostRepositoryCustom postRepositoryCustom;
    private final RedisService redisService;
    private final MemberService memberService;

    private final StringRedisTemplate stringRedisTemplate;
    private final RedisTemplate<String, Object> redisTemplate;

    public static final int CACHE_MAX_SIZE = 50;


    public CommonResponse<?> findBooks(String type, String search, Pageable pageable) {

        Page<SearchResponse.BookSearchResult> books;

        if ("title".equals(type)) {
            books = bookRepository.findByTitleContain(search, pageable);
        } else if ("author".equals(type)) {
            books = bookRepository.findByAuthorContain(search, pageable);
        } else {
            throw new BaseException(ExceptionCode.NOT_MATCHED_TYPE_PARAMETER);
        }
        if (books.isEmpty()) {
            return CommonResponse.ok("검색 결과가 없습니다.");
        }



        return CommonResponse.ok(books);
    }

    @Transactional(readOnly = true)
    public CommonResponse<?> findPosts(
            String type,
            String search,
            String sortField,
            int size,
            int page,
            JwtUserDetails userDetails
    ) {

        String orderByQuery = getOrderByQuery(sortField);

        String query = getSearchQuery(type, search);

        //캐시먼저검사 (look aside)
        List<PostResponseDTO> posts;

        if(type.equals("category") && page<3){
            String key = type+"_"+search;

            //캐시검사
            posts = getCachePosts(key,type, search, size, page);

            if(posts==null){

                posts = postRepositoryCustom.findPostsByDynamicQuery(search, query, orderByQuery,size,page);

                for (PostResponseDTO p : posts) {
                    redisTemplate.opsForZSet().add(key,p,p.getLikesCount());
                }
            }
        }else{
            posts = postRepositoryCustom.findPostsByDynamicQuery(search, query, orderByQuery, size, page);
        }

        if (posts.isEmpty()) {
            return CommonResponse.ok("검색 결과가 없습니다.");
        }

        List<Long> postIds= posts.stream()
                .map(PostResponseDTO::getPostId)
                .toList();

        List<InteractionResponseDTO> interactions = memberService.checkInteractions(postIds, userDetails.id());

        Long totalCount = postRepositoryCustom.totalCount(query,search);


        int totalPage = (int)Math.ceil(totalCount /(double)size);
        boolean hasNextPage = (page+1) <totalPage;


        return CommonResponse.ok(new PostResponse.PostResults(posts,interactions,totalPage,hasNextPage));
    }

    private List<PostResponseDTO> getCachePosts(String key, String type, String search, int size, int page) {

        List<PostResponseDTO> posts;
        posts = searchResultCache(type, search, page, size);
        int startIndex = page*size;
        if(posts.size() != 10) {

            redisTemplate.opsForZSet().removeRange(key, startIndex, - 1);
            return null;
        }

        return posts;
    }

    private String getSearchQuery(String type, String search) {

        String query;
        switch (type){
            case "title" -> {
                query=" b.title = :search ";
            }

            case "category" -> {
                query=" p.category = :search ";
            }

            case "tag" -> {
                redisService.saveOrUpdateKeyword("search", search);
                query= "ph.post_id IN (" +
                        "  SELECT ph.post_id " +
                        "  FROM post_hashtag ph " +
                        "  INNER JOIN hashtag h ON h.id = ph.hashtag_id " +
                        "  WHERE h.name = :search" +
                        ") ";
            }
            default ->  throw new BaseException(ExceptionCode.NOT_MATCHED_TYPE_PARAMETER);

        }
        return query;
    }

    private static String getOrderByQuery(String sortField) {

        String orderByQuery  = " like_count DESC ";

        if(sortField.equals("create_at")){
            orderByQuery  = " p.create_at DESC ";
        }
        return orderByQuery;
    }



    public List<PostResponseDTO> searchResultCache(String type, String search,int page, int size){

        List<PostResponseDTO> posts;

        String key = type+"_"+search;
        int start = page * size;
        int end = (page+1) * size -1;

        Set<Object> test= redisTemplate.opsForZSet().reverseRange(key,start ,end);

        posts = test.stream().map(o-> (PostResponseDTO)o).toList();

        return posts;

    }


    public List<String> getRelatedHashtags(String prefix) {
        String min = "";
        String max = prefix + "\uFFFF";

        Range.Bound<String> lowerBound = Range.Bound.inclusive(min);
        Range.Bound<String> upperBound = Range.Bound.inclusive(max);
        Range<String> range = Range.of(lowerBound, upperBound);

        Set<String> allTags = stringRedisTemplate.opsForZSet().rangeByLex("hashtags", range);

        return allTags.stream()
                .filter(tag -> tag.contains(prefix))
                .sorted((tag1, tag2) -> {
                    boolean startsWith1 = tag1.startsWith(prefix);
                    boolean startsWith2 = tag2.startsWith(prefix);

                    if (startsWith1 && startsWith2) {
                        return tag1.compareTo(tag2);
                    }
                    if (startsWith1) return -1;
                    if (startsWith2) return 1;
                    return tag1.compareTo(tag2);
                })
                .collect(Collectors.toList());
    }

    public CommonResponse<?> getFamousTags() {

        Set<String> search =stringRedisTemplate.opsForZSet().reverseRange(FAMOUS_SEARCH_TAG_KEY, 0, 6 - 1);
        Set<String> record =stringRedisTemplate.opsForZSet().reverseRange(FAMOUS_RECORD_TAG_KEY, 0, 6 - 1);

        return CommonResponse.ok(new SearchResponse.HashTagRank(search,record)) ;
    }



}
