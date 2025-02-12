package today.todaysentence.domain.member.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import today.todaysentence.util.annotation.*;

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
            @Override
            public String toString() {
                return "CheckPassword {" +
                        "password='[PROTECTED]'" +
                        '}';
            }
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
        @Override
        public String toString() {
            return "SignUp {" +
                    "email='" + email + '\'' +
                    "nickname='" + nickname + '\'' +
                    ", password='[PROTECTED]'" +
                    '}';
        }
    }

    public record SignIn(
            @NotBlank
            @Email
            String email,
            @NotBlank
            @ValidPassword
            String password) {
        @Override
        public String toString() {
            return "SignIn {" +
                    "email='" + email + '\'' +
                    ", password='[PROTECTED]'" +
                    '}';
        }
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
