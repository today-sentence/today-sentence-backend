package today.todaysentence.global.exception.advice;

import static today.todaysentence.global.exception.exception.ExceptionCode.PARAMETER_VALIDATION_FAIL;

import today.todaysentence.global.exception.exception.BaseException;

public record ErrorResponse(String message) {

    static ErrorResponse from(BaseException exception) {
        return new ErrorResponse(exception.getMessage());
    }

    static ErrorResponse parameter() {
        return new ErrorResponse(PARAMETER_VALIDATION_FAIL.getMessage());
    }
}
