package today.todaysentence.domain.post.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import today.todaysentence.domain.book.dto.BookInfo;
import today.todaysentence.domain.category.Category;
import today.todaysentence.domain.member.Member;
import org.springframework.transaction.annotation.Transactional;
import today.todaysentence.domain.book.Book;
import today.todaysentence.domain.book.service.BookService;
import today.todaysentence.domain.hashtag.Hashtag;
import today.todaysentence.domain.hashtag.service.HashtagService;
import today.todaysentence.domain.post.Post;
import today.todaysentence.domain.post.dto.PostRequest;
import today.todaysentence.domain.post.dto.PostResponse;
import today.todaysentence.domain.post.dto.ScheduledPosts;
import today.todaysentence.domain.post.repository.PostQueryRepository;
import today.todaysentence.domain.post.repository.PostRepository;
import today.todaysentence.global.exception.exception.ExceptionCode;
import today.todaysentence.global.exception.exception.PostException;
import today.todaysentence.global.response.CommonResponse;
import today.todaysentence.global.security.userDetails.CustomUserDetails;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Set;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class PostService {
    private final BookService bookService;
    private final HashtagService hashtagService;
    private final PostRepository postRepository;
    private final PostQueryRepository postQueryRepository;

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
    public CommonResponse<PostResponse.Statistics> getStatistics(CustomUserDetails userDetails) {

        List<PostResponse.CategoryCount> recordsStatistics =postRepository.findByMemberRecordsStatistics(userDetails.member().getId());
        List<PostResponse.CategoryCount> bookmarkStatistics =postRepository.findByMemberBookmarksStatistics(userDetails.member().getId());

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
}
