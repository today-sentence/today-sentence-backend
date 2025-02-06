package today.todaysentence.domain.post.dto;

import today.todaysentence.domain.book.dto.BookInfo;

public class PostResponse {

    public record Summary(
            Long postId,
            String bookTitle,
            String bookAuthor,
            String bookPublisher,
            int bookPublishingYear,
            String bookCover
    ) {
        public Summary(Long postId, BookInfo bookInfo) {
            this(postId,
                    bookInfo.title(),
                    bookInfo.author(),
                    bookInfo.publisher(),
                    bookInfo.publishingYear(),
                    bookInfo.cover());
        }
    }

    public record Detail(

    ) {
    }
}
