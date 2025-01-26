package today.todaysentence.domain.user.dto;

public class UserResponse {

    public record Success() {
    }

    public record Join(
            Long id,
            String email,
            String nickname
    ) {
    }
}
