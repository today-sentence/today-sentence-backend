package today.todaysentence.domain.search.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Range;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import today.todaysentence.domain.book.repository.BookRepository;
import today.todaysentence.domain.post.repository.PostRepository;
import today.todaysentence.domain.post.repository.PostRepositoryCustom;
import today.todaysentence.domain.search.dto.SearchResponse;
import today.todaysentence.global.exception.exception.BaseException;
import today.todaysentence.global.exception.exception.ExceptionCode;
import today.todaysentence.global.redis.RedisService;
import today.todaysentence.global.response.CommonResponse;


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

    private final StringRedisTemplate stringRedisTemplate;


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

    public CommonResponse<?> findPosts(String type, String search ) {

        List<SearchResponse.PostSearchResult> posts;

        String query;
        if ("title".equals(type)) {
            query="b.title = '" + search + "'";
            posts = postRepositoryCustom.findPostsByDynamicQuery(query);
        }else if ("category".equals(type)) {
            query="p.category = '" + search + "'";
            posts = postRepositoryCustom.findPostsByDynamicQuery(query);
        } else if ("tag".equals(type)) {
            redisService.saveOrUpdateKeyword("search",search);
            query= "ph.post_id IN (" +
                    "  SELECT ph.post_id " +
                    "  FROM post_hashtag ph " +
                    "  INNER JOIN hashtag h ON h.id = ph.hashtag_id " +
                    "  WHERE h.name = '" +search +"'"+
                    ") ";
                    posts = postRepositoryCustom.findPostsByDynamicQuery(query);
        }else{
            throw new BaseException(ExceptionCode.NOT_MATCHED_TYPE_PARAMETER);
        }
        if (posts.isEmpty()) {
            return CommonResponse.ok("검색 결과가 없습니다.");
        }


        return CommonResponse.ok(posts);
    }

    /**
     * 1.사전순으로 레디스에 넣은정보를 전부꺼내와서 스프링에서 재정렬후 리턴
     * 2.추후 검색어만 앞글자에 포함한 결과를 리턴
     *    ㄴ 레디스에서 range검색
     * @param prefix (검색단어)
     * @return List . 검색결과 1.검색어앞글자 우선 2.검색어 포함글자 뒤
     *
     */
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
