package today.todaysentence.domain.post.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import today.todaysentence.domain.book.dto.BookInfo;
import today.todaysentence.domain.member.Member;
import org.springframework.transaction.annotation.Transactional;
import today.todaysentence.domain.book.Book;
import today.todaysentence.domain.book.service.BookService;
import today.todaysentence.domain.hashtag.Hashtag;
import today.todaysentence.domain.hashtag.service.HashtagService;
import today.todaysentence.domain.post.Post;
import today.todaysentence.domain.post.dto.PostRequest;
import today.todaysentence.domain.post.dto.PostResponse;
import today.todaysentence.domain.post.repository.PostQueryRepository;
import today.todaysentence.domain.post.repository.PostRepository;
import today.todaysentence.global.exception.exception.ExceptionCode;
import today.todaysentence.global.exception.exception.PostException;

import java.util.List;

@RequiredArgsConstructor
@Service
public class PostService {
    private final BookService bookService;
    private final HashtagService hashtagService;
    private final PostRepository postRepository;
    private final PostQueryRepository postQueryRepository;

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
        List<Post> posts = postRepository.findMyPostsByDate(member, month, year);

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
}
