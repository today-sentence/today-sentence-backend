package today.todaysentence.domain.comment.dto;

import java.time.LocalDateTime;
import java.util.List;

public class CommentResponse {

    public record CommentInfos(
            List<CommentInfo> comments,
            int offset,
            int size,
            boolean hasNext
    ) {
    }

    public record CommentInfo(
            String nickname,
            String content,
            LocalDateTime createdAt
    ) {
    }
}
