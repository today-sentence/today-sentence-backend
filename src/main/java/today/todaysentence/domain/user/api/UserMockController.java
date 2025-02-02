package today.todaysentence.domain.user.api;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import today.todaysentence.domain.user.dto.UserRequest;
import today.todaysentence.domain.user.dto.UserResponse;
import today.todaysentence.global.response.CommonResponse;
import today.todaysentence.global.swagger.UserApiSpec;

@RestController
public class UserMockController implements UserApiSpec {

    @PostMapping("/users/check-email")
    public CommonResponse<?> checkEmail(@RequestBody @Valid UserRequest.CheckEmail request) {
        return CommonResponse.success();
    }

    @PostMapping("/users/check-nickname")
    @ResponseStatus(HttpStatus.OK)
    public CommonResponse<?> checkNickname(@RequestBody @Valid UserRequest.CheckNickname request) {
        return CommonResponse.success();
    }

    @PostMapping("/users/check-password")
    public CommonResponse<?> checkPassword(@RequestBody @Valid UserRequest.CheckPassword request) {
        return CommonResponse.success();
    }

    @PostMapping("/users/join")
    @ResponseStatus(HttpStatus.CREATED)
    public CommonResponse<UserResponse.Join> join(@RequestBody @Valid UserRequest.Join request) {
        return CommonResponse.ok(new UserResponse.Join(1L, request.email(), request.nickname()));
    }
}
