//package today.todaysentence.domain.member.api;
//
//import jakarta.validation.Valid;
//import org.springframework.http.HttpStatus;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.ResponseStatus;
//import org.springframework.web.bind.annotation.RestController;
//import today.todaysentence.domain.member.dto.MemberRequest;
//import today.todaysentence.domain.member.dto.MemberResponse;
//import today.todaysentence.global.response.CommonResponse;
//import today.todaysentence.global.swagger.MemberApiSpec;
//
//@RestController
//public class MemberMockController implements MemberApiSpec {
//
//    @PostMapping("/users/check-email")
//    public CommonResponse<?> checkEmail(@RequestBody @Valid MemberRequest.CheckEmail request) {
//        return CommonResponse.success();
//    }
//
//    @PostMapping("/users/check-nickname")
//    @ResponseStatus(HttpStatus.OK)
//    public CommonResponse<?> checkNickname(@RequestBody @Valid MemberRequest.CheckNickname request) {
//        return CommonResponse.success();
//    }
//
//    @PostMapping("/users/check-password")
//    public CommonResponse<?> checkPassword(@RequestBody @Valid MemberRequest.CheckPassword request) {
//        return CommonResponse.success();
//    }
//
//    @PostMapping("/users/join")
//    @ResponseStatus(HttpStatus.CREATED)
//    public CommonResponse<MemberResponse.Join> join(@RequestBody @Valid MemberRequest.SignUp request) {
//        return CommonResponse.ok(new MemberResponse.Join(1L, request.email(), request.nickname()));
//    }
//}
