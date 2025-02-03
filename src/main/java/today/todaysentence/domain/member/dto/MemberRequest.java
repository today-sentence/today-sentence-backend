package today.todaysentence.domain.member.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import today.todaysentence.global.anotation.ValidPassword;

public class MemberRequest {

    public record CheckEmail(
            @NotBlank
            @Email
            String email) {
    }

    public record CheckNickname(@NotBlank String nickname) {
    }

    public record CheckPassword(
            @NotBlank
            @ValidPassword
            String password) {
    }

    public record SignUp(
            @NotBlank
            @Email
            String email,
            @NotBlank
            String nickname,
            @NotBlank
            @ValidPassword
            String password) {
    }

    public record SignIn(
            @NotBlank
            @Email
            String email,
            @NotBlank
            @ValidPassword
            String password) {
    }
}
