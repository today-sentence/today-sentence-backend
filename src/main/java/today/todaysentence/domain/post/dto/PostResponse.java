package today.todaysentence.domain.post.dto;

public class PostResponse {

    public record Summary(
            Long postId,
            String bookTitle,
            String bookAuthor,
            String bookPublisher,
            int bookPublishingYear,
            String bookCover
    ) {
    }

    public record Detail(
        
    ) {
    }
}
