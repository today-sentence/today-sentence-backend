package today.todaysentence.domain.bookmark.dto;

import jakarta.validation.constraints.NotNull;

public class BookmarkRequest {

    public record Save(@NotNull Long postId) {
    }
}
