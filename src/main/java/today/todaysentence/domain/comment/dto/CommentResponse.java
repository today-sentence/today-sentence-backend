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
            Long id,
            String nickname,
            String content,
            String profileImage,
            LocalDateTime createdAt
    ) {
    }
}
