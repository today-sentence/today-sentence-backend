package today.todaysentence.global.exception.exception;

public class BaseException extends RuntimeException {
    private final ExceptionCode exceptionCode;

    public BaseException(ExceptionCode exceptionCode) {
        super(exceptionCode.getMessage());
        this.exceptionCode = exceptionCode;
    }

    public int getHttpStatus() {
        return this.exceptionCode.getStatus();
    }
}
