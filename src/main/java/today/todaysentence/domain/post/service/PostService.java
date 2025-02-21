package today.todaysentence.domain.post.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import today.todaysentence.domain.book.Book;
import today.todaysentence.domain.book.dto.BookInfo;
import today.todaysentence.domain.book.service.BookService;
import today.todaysentence.domain.category.Category;
import today.todaysentence.domain.hashtag.Hashtag;
import today.todaysentence.domain.hashtag.service.HashtagService;
import today.todaysentence.domain.member.Member;
import today.todaysentence.domain.member.dto.InteractionResponseDTO;
import today.todaysentence.domain.member.service.MemberService;
import today.todaysentence.domain.post.Post;
import today.todaysentence.domain.post.dto.PostRequest;
import today.todaysentence.domain.post.dto.PostResponse;
import today.todaysentence.domain.post.dto.PostResponseDTO;
import today.todaysentence.domain.post.dto.ScheduledPosts;
import today.todaysentence.domain.post.repository.PostQueryRepository;
import today.todaysentence.domain.post.repository.PostRepository;
import today.todaysentence.domain.post.repository.PostRepositoryCustom;
import today.todaysentence.global.exception.exception.BaseException;
import today.todaysentence.global.exception.exception.ExceptionCode;
import today.todaysentence.global.exception.exception.PostException;
import today.todaysentence.global.response.CommonResponse;
import today.todaysentence.global.security.userDetails.CustomUserDetails;
import today.todaysentence.global.security.userDetails.JwtUserDetails;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@RequiredArgsConstructor
@Service
public class PostService {
    private final BookService bookService;
    private final HashtagService hashtagService;
    private final PostRepository postRepository;
    private final PostRepositoryCustom postRepositoryCustom;
    private final PostQueryRepository postQueryRepository;
    private final RedisTemplate<String,Object> redisTemplate;
    private final MemberService memberService;


    final int FIRST_RANK_LIMIT = 4;
    final int SECOND_RANK_LIMIT = 9;



    public Post findRandomPostNotInProvided(Member member, List<Long> recommendedPostIds) {
        return postQueryRepository.findOneNotInRecommended(member, recommendedPostIds)
                .orElseThrow(() -> new PostException(ExceptionCode.POST_NOT_FOUND));
    }

    @Transactional
    public void record(PostRequest.Record dto, Member member) {
        Book book = bookService.findOrCreate(PostMapper.toBook(dto));
        List<Hashtag> hashtags = hashtagService.findOrCreate(dto.hashtags());

        postRepository.save(PostMapper.toEntity(member, book, hashtags, dto));
    }

    public List<PostResponse.Summary> getMyPostsByDate(Member member, int month, int year) {
        LocalDateTime startDate = LocalDateTime.of(year, month, 1, 0, 0);
        LocalDateTime endDate = startDate.plusMonths(1).minusSeconds(1);

        List<Post> posts = postRepository.findMyPostsByDate(member, startDate, endDate);

        return posts.stream()
                .map(post -> PostMapper.toSummary(
                        post.getId(),
                        bookService.getBookInfo(post.getBook())
                ))
                .toList();
    }

    public PostResponse.Summary toSummary(Long postId) {
        Post post = findPost(postId);

        return PostMapper.toSummary(postId, bookService.getBookInfo(post.getBook()));
    }

    public PostResponse.Detail getPostDetail(Long postId) {
        Post post = findPost(postId);
        BookInfo bookInfo = bookService.getBookInfo(post.getBook());

        return PostMapper.toDetail(post, bookInfo);
    }

    private Post findPost(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new PostException(ExceptionCode.POST_NOT_FOUND));
    }

    public void isValidPost(Long postId) {
        if (postRepository.existsById(postId)) {
            return;
        }

        throw new PostException(ExceptionCode.POST_NOT_FOUND);
    }


    @Transactional(readOnly = true)
    public CommonResponse<PostResponse.Statistics> getStatistics(JwtUserDetails userDetails) {

        List<PostResponse.CategoryCount> recordsStatistics =postRepository.findByMemberRecordsStatistics(userDetails.id());
        List<PostResponse.CategoryCount> bookmarkStatistics =postRepository.findByMemberBookmarksStatistics(userDetails.id());

        Map<Category, Long> records = new EnumMap<>(Category.class);
        Map<Category, Long> bookmarks = new EnumMap<>(Category.class);

        for(Category c : Category.values()){
            records.put(c,0L);
            bookmarks.put(c,0L);
        }
        recordsStatistics.forEach(s->records.put(s.category(),s.count()));
        bookmarkStatistics.forEach(s->bookmarks.put(s.category(),s.count()));


        return CommonResponse.ok(new PostResponse.Statistics(records, bookmarks));

    }

    public List<ScheduledPosts> fetchScheduledPostsByEachCategory(int countForEach, Set<Long> duplicatedIds) {
        return Arrays.stream(Category.values())
                .map(Category::name)
                .map(category -> postRepository.findRandomPostsByCategoryAndNotInIds(category, duplicatedIds, countForEach))
                .filter(posts -> !posts.isEmpty())
                .map(PostMapper::toScheduledPosts)
                .toList();
    }


    @Transactional
    public CommonResponse<PostResponse.PostResult> getTodaySentence(CustomUserDetails userDetails) {

        Member member =userDetails.member();

        Long randomPostId;

        if(member.getTodayPostId() == null){

            //step1 - 조회한 멤버의 관심사를 가져옵니다
            Map<Category, Long> interestRank = memberInterestRank(member);

            //step2 - 오늘의 명언리스트들중 유저의 아이디와 겹치지않는것들만 가저옵니다.
            Set<Long> postIds;
            postIds = getCachingData(interestRank, member,FIRST_RANK_LIMIT,0);

            if (postIds.isEmpty()) {

                //step 5? step2의 결과가없을시
                postIds = getCachingData(interestRank, member, SECOND_RANK_LIMIT - FIRST_RANK_LIMIT, FIRST_RANK_LIMIT);

                if (postIds.isEmpty()) {
                    //그래도없을시
                    String query = " p.writer_id != " + member.getId() ;
                    PostResponseDTO result = postRepositoryCustom.findPostByNotMatchMember(query);
                    InteractionResponseDTO interaction = memberService.checkInteraction(result.getPostId(), member.getId());
                    return CommonResponse.ok(new PostResponse.PostResult(result,interaction));
                }
            }

            //step3 - 가저온 리스트들중 랜덤한개를뽑습니다.
            randomPostId = postIds.stream()
                    .skip(ThreadLocalRandom.current().nextInt(postIds.size()))
                    .findFirst()
                    .orElse(null);

            member.insertTodaySentenceId(randomPostId);

        }else{

            randomPostId=member.getTodayPostId();

        }

        //step4 캐싱값 확인 CacheHit >> 바로반환  CacheMiss >>  DATABASE 조회후 캐싱후 반환
        return Optional.ofNullable((PostResponseDTO)redisTemplate.opsForValue().get("postId : " + randomPostId))
                .map(post -> new PostResponse.PostResult(post, memberService.checkInteraction(randomPostId, member.getId())))
                .or(() -> {
                    String query = "p.id = " + randomPostId;
                    PostResponseDTO result = postRepositoryCustom.findPostByDynamicQuery(query);
                    InteractionResponseDTO interaction = memberService.checkInteraction(randomPostId, member.getId());

                    redisTemplate.opsForValue().set("postId : " + randomPostId, result, 15, TimeUnit.MINUTES);

                    return Optional.of(new PostResponse.PostResult(result, interaction));
                })
                .map(CommonResponse::ok)
                .orElseThrow(() -> new BaseException(ExceptionCode.POST_NOT_FOUND));
    }

    private Map<Category, Long> memberInterestRank(Member member) {
        List<PostResponse.CategoryCount> interestCategory = postRepository.findByMemberAllStatistics(member.getId());

        Map<Category,Long> interestRank = new EnumMap<>(Category.class);
        for(Category c : Category.values()){
            interestRank.put(c,0L);
        }

        interestCategory.forEach(r->interestRank.put(r.category(),r.count()));
        return interestRank;
    }

    private Set<Long> getCachingData(Map<Category, Long> interestRank, Member member,int rank, int skipRank) {

        return interestRank.keySet().stream()
                .sorted((c1, c2) -> interestRank.get(c2).compareTo(interestRank.get(c1)))
                .skip(skipRank)
                .limit(rank)
                .flatMap(c->{
                    List<Long> memberIds = (List<Long>) redisTemplate.opsForHash().get(c.name(), "writer_id");

                    if (memberIds == null) {
                        return Stream.empty();
                    }

                    List<Integer> notDuplicateIndex =
                            IntStream.range(0, memberIds.size())
                                    .filter(i -> !memberIds.get(i).equals(member.getId()))
                                    .boxed()
                                    .toList();

                    List<Long> postIdsList = (List<Long>) redisTemplate.opsForHash().get(c.name(), "post_id");

                    if (postIdsList == null) {
                        return Stream.empty();
                    }

                    return notDuplicateIndex.stream()
                            .map(postIdsList::get);
                        }
                )
                .collect(Collectors.toSet());
    }



}
