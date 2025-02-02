package today.todaysentence.global.exception.exception;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ExceptionCode {

    // user
    INVALID_EMAIL_FORMAT("이메일의 형식이 올바르지 않습니다.", BAD_REQUEST),
    PASSWORD_ERROR("비밀번호를 확인해주세요.", BAD_REQUEST),

    // post
    POST_NOT_FOUND("게시된 명언 글을 찾을 수 없습니다.", NOT_FOUND),

    //
    PARAMETER_VALIDATION_FAIL("파라미터가 올바르지 않습니다.", BAD_REQUEST),
    ;

    private final String message;
    private final HttpStatus status;

    public int getStatus() {
        return status.value();
    }
}
