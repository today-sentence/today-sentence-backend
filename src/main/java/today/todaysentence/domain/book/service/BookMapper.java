package today.todaysentence.domain.book.service;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import today.todaysentence.domain.book.Book;
import today.todaysentence.domain.book.dto.BookInfo;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BookMapper {

    static BookInfo toBookInfo(Book book) {
        return new BookInfo(
                book.getTitle(),
                book.getAuthor(),
                book.getCover(),
                book.getPublisher(),
                book.getPublishingYear()
        );
    }
}
