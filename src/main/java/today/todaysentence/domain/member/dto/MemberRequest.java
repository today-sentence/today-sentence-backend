package today.todaysentence.domain.member.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import today.todaysentence.util.annotation.ValidMessage;
import today.todaysentence.util.annotation.ValidNickname;
import today.todaysentence.util.annotation.ValidPassword;

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
    public record FindEmail(
            @NotBlank
            String nickname ){
    }
    public record FindPassword(
            @NotBlank
            @Email
            String email ){
    }

    public record VerifyCodeCheck(
            @NotBlank
            @Email
            String email,
            @NotBlank
            String code
    ){
    }
}
