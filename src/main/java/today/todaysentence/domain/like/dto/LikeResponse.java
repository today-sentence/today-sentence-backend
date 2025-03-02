package today.todaysentence.domain.like.dto;

public class LikeResponse {
    public record like(
        boolean like
    ){
    }

    public record LikeEvent(
        Long postId
    ){
    }



}
