package today.todaysentence.domain.comment.dto;

import jakarta.validation.constraints.NotBlank;

public class CommentRequest {

    public record Create(
            @NotBlank
            String content
    ) {
    }
}
