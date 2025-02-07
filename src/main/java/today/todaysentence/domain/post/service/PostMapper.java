package today.todaysentence.domain.post.service;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import today.todaysentence.domain.book.Book;
import today.todaysentence.domain.book.dto.BookInfo;
import today.todaysentence.domain.category.Category;
import today.todaysentence.domain.hashtag.Hashtag;
import today.todaysentence.domain.member.Member;
import today.todaysentence.domain.post.Post;
import today.todaysentence.domain.post.dto.PostRequest;
import today.todaysentence.domain.post.dto.PostResponse;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PostMapper {

    public static Book toBook(PostRequest.Record dto) {
        return new Book(
                dto.isbn(),
                dto.bookTitle(),
                dto.bookAuthor(),
                dto.bookCover(),
                dto.bookPublisher(),
                dto.bookPublishingYear()
        );
    }

    public static Post toEntity(Member member, Book book, List<Hashtag> hashtags, PostRequest.Record dto) {
        return new Post(
                member,
                book,
                dto.category(),
                hashtags,
                dto.content()
        );
    }

    public static PostResponse.Summary toSummary(Long postId, BookInfo bookInfo) {
        return PostResponse.Summary.builder()
                .postId(postId)
                .bookTitle(bookInfo.title())
                .bookAuthor(bookInfo.author())
                .bookPublisher(bookInfo.publisher())
                .bookPublishingYear(bookInfo.publishingYear())
                .bookCover(bookInfo.cover())
                .build();
    }

    public static PostResponse.Detail toDetail(Post post, BookInfo bookInfo) {
        return PostResponse.Detail.builder()
                .postId(post.getId())
                .bookTitle(bookInfo.title())
                .bookAuthor(bookInfo.author())
                .bookPublisher(bookInfo.publisher())
                .bookPublishingYear(bookInfo.publishingYear())
                .bookCover(bookInfo.cover())
                .category(post.getCategory().name())
                .hashtags(post.getHashtagNames())
                .build();
    }
}
