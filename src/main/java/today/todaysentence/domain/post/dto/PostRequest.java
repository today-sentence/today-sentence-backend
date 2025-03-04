package today.todaysentence.domain.post.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import today.todaysentence.domain.category.Category;

import java.util.List;

public class PostRequest {

    public record Record(
            @NotBlank
            String bookTitle,
            @NotBlank
            String bookAuthor,
            @NotBlank
            String bookPublisher,
            @NotNull
            Integer bookPublishingYear,
            @NotBlank
            String bookCover,
            @NotNull
            String isbn,
            Category category,
            List<String> hashtags,
            @NotBlank
            String content
    ) {
    }
}
