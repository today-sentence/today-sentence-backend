package today.todaysentence.domain.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class UserRequest {

    public record CheckEmail(@NotBlank @Email String email) {
    }

    public record CheckNickname(@NotBlank String nickname) {
    }

    public record CheckPassword(@NotBlank String password) {
    }

    public record Join(
            @NotBlank @Email String email,
            @NotBlank String nickname,
            @NotBlank String password) {
    }
}
