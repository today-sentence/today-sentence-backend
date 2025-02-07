package today.todaysentence.domain.member.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import today.todaysentence.global.annotation.ValidMessage;
import today.todaysentence.global.annotation.ValidNickname;
import today.todaysentence.global.annotation.ValidPassword;

public class MemberRequest {

    public record CheckEmail(
            @NotBlank
            @Email
            String email) {
    }

    public record CheckNickname(
            @NotBlank
            @ValidNickname
            String nickname
    ) {
    }

    public record CheckPassword(
            @NotBlank
            @ValidPassword
            String password) {
    }
    public record VerificationPassword(
            @NotBlank
            String password) {
    }
    public record CheckMessage(
            @NotBlank
            @ValidMessage
            String message) {
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
