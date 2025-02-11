package today.todaysentence.domain.comment.dto;

import java.time.LocalDateTime;

public class CommentResponse {

    public record CommentInfo(
            String nickname,
            String content,
            LocalDateTime createdAt
    ) {
    }
}
