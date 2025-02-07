package today.todaysentence.domain.post.dto;

import lombok.Builder;

import java.util.List;

public class PostResponse {

    @Builder
    public record Summary(
            Long postId,
            String bookTitle,
            String bookAuthor,
            String bookPublisher,
            int bookPublishingYear,
            String bookCover
    ) {
    }

    @Builder
    public record Detail(
            Long postId,
            String bookTitle,
            String bookAuthor,
            String bookPublisher,
            int bookPublishingYear,
            String bookCover,
            String category,
            List<String> hashtags
            // 추후 공감, 댓글, 저장 필드도 추가해야합니다!
    ) {
    }
}
