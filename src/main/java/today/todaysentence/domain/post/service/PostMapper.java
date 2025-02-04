package today.todaysentence.domain.post.service;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import today.todaysentence.domain.book.Book;
import today.todaysentence.domain.category.Category;
import today.todaysentence.domain.hashtag.Hashtag;
import today.todaysentence.domain.member.Member;
import today.todaysentence.domain.post.Post;
import today.todaysentence.domain.post.dto.PostRequest;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PostMapper {

    static Book toBook(PostRequest.Record dto) {
        return new Book(
                dto.isbn(),
                dto.bookTitle(),
                dto.bookAuthor(),
                dto.bookCover(),
                dto.bookPublisher(),
                dto.bookPublishingYear()
        );
    }

    static Post toEntity(Member member, Book book, Category category, List<Hashtag> hashtags, PostRequest.Record dto) {
        return new Post(
                member,
                book,
                category,
                hashtags,
                dto.content()
        );
    }
}
