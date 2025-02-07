package today.todaysentence.global.exception.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

@Getter
@RequiredArgsConstructor
public enum ExceptionCode {

    // member
    INVALID_EMAIL_FORMAT("이메일의 형식이 올바르지 않습니다.", BAD_REQUEST),
    PASSWORD_ERROR("비밀번호를 확인해주세요.", BAD_REQUEST),
    MEMBER_NOT_FOUND("사용자를 찾을 수 없습니다.", NOT_FOUND),
    DUPLICATED_EMAIL("이미 사용중인 이메일 입니다.", BAD_REQUEST),
    DUPLICATED_NICKNAME("이미 사용중인 닉네임 입니다.", BAD_REQUEST),
    NOT_MATCHED_INFORMATION("사용자 정보가 일치하지 않습니다..", BAD_REQUEST),

    // post
    POST_NOT_FOUND("게시된 명언 글을 찾을 수 없습니다.", NOT_FOUND),

    //
    PARAMETER_VALIDATION_FAIL("파라미터가 올바르지 않습니다.", BAD_REQUEST),

    INVALID_TOKEN("유효하지 않은 토큰입니다.",UNAUTHORIZED),
    EXPIRED_TOKEN("만료된 토큰입니다.",UNAUTHORIZED),
    UNSUPPORTED_TOKEN("지원되지 않는 토큰입니다.",UNAUTHORIZED),
    WRONG_TOKEN("잘못된 토큰입니다.",UNAUTHORIZED),
    NOT_ACCORDANCE_DEVICE_ID("기존접속과 정보가 다릅니다.",BAD_REQUEST),
    MEMBER_ALREADY_WITHDRAWN("이미 탈퇴한 회원입니다.",BAD_REQUEST );

    private final String message;
    private final HttpStatus status;

    public int getStatus() {
        return status.value();
    }
}
