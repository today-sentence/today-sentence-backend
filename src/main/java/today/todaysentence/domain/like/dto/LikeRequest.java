package today.todaysentence.domain.like.dto;

import jakarta.validation.constraints.NotNull;

public class LikeRequest {
    public record LikePostRequest(
            @NotNull(message = "Id 값이 없습니다.postId를 포함해 주세요.")
            Long postId
    ){

    }
}
