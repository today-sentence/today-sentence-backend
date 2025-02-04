package today.todaysentence.global.exception.advice;

import static today.todaysentence.global.exception.exception.ExceptionCode.PARAMETER_VALIDATION_FAIL;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import today.todaysentence.global.exception.exception.BaseException;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {
    private String message;

    static ErrorResponse from(BaseException exception) {
        return new ErrorResponse(exception.getMessage());
    }

    static ErrorResponse parameter() {
        return new ErrorResponse(PARAMETER_VALIDATION_FAIL.getMessage());
    }
}
