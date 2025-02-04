package today.todaysentence.domain.post.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import today.todaysentence.domain.member.Member;
import org.springframework.transaction.annotation.Transactional;
import today.todaysentence.domain.book.Book;
import today.todaysentence.domain.book.service.BookService;
import today.todaysentence.domain.category.Category;
import today.todaysentence.domain.category.service.CategoryService;
import today.todaysentence.domain.hashtag.Hashtag;
import today.todaysentence.domain.hashtag.service.HashtagService;
import today.todaysentence.domain.post.Post;
import today.todaysentence.domain.post.dto.PostRequest;
import today.todaysentence.domain.post.repository.PostQueryRepository;
import today.todaysentence.domain.post.repository.PostRepository;
import today.todaysentence.domain.user.User;
import today.todaysentence.global.exception.exception.ExceptionCode;
import today.todaysentence.global.exception.exception.PostException;

import java.util.List;

@RequiredArgsConstructor
@Service
public class PostService {
    private final BookService bookService;
    private final HashtagService hashtagService;
    private final CategoryService categoryService;
    private final PostRepository postRepository;
    private final PostQueryRepository postQueryRepository;

    public Post findRandomPostNotInProvided(Member member, List<Long> recommendedPostIds) {
        return postQueryRepository.findOneNotInRecommended(member, recommendedPostIds)
                .orElseThrow(() -> new PostException(ExceptionCode.POST_NOT_FOUND));
    }

    @Transactional
    public void record(PostRequest.Record dto) {
        Book book = bookService.findOrCreate(PostMapper.toBook(dto));
        List<Hashtag> hashtags = hashtagService.findOrCreate(dto.hashtags());
        Category category = categoryService.toCategory(dto.category());

        postRepository.save(PostMapper.toEntity(new User("email"), book, category, hashtags, dto));
    }
}
