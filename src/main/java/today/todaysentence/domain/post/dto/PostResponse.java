package today.todaysentence.domain.post.dto;

import lombok.Builder;
import lombok.Getter;
import today.todaysentence.domain.category.Category;

import java.util.List;
import java.util.Map;

public class PostResponse {

    @Builder
    public record Summary(
            Long postId,
            String bookTitle,
            String bookAuthor,
            String bookPublisher,
            Integer bookPublishingYear,
            String bookCover
    ) {
    }

    @Builder
    public record Detail(
            Long postId,
            String bookTitle,
            String bookAuthor,
            String bookPublisher,
            Integer bookPublishingYear,
            String bookCover,
            String category,
            List<String> hashtags
            // 추후 공감, 댓글, 저장 필드도 추가해야합니다!
    ) {
    }

    @Builder
    public record CategoryCount(
            Category category,
            Long count

    ){
    }

    public record Statistics(
            Map<Category,Long> records,
            Map<Category,Long> bookmarks
    ){
    }
}
