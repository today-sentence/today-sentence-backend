package today.todaysentence.global.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Map;

@Getter
@JsonInclude(Include.NON_NULL)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class CommonResponse<T> {
    private final T data;
    private final String message;

    public static <T> CommonResponse<T> ok(T data) {
        return new CommonResponse<>(data, null);
    }

    public static CommonResponse<?> success() {
        return new CommonResponse<>(Map.of("success", true), null);
    }
}
