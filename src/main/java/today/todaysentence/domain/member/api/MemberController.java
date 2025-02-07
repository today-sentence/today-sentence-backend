package today.todaysentence.domain.member.api;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import today.todaysentence.domain.member.dto.MemberRequest;
import today.todaysentence.domain.member.service.MemberService;
import today.todaysentence.global.response.CommonResponse;
import today.todaysentence.global.security.userDetails.CustomUserDetails;
import today.todaysentence.global.swagger.MemberApiSpec;

@RestController
@RequestMapping("/api/member")
@RequiredArgsConstructor
@Slf4j
public class MemberController implements MemberApiSpec {

    private final MemberService memberService;

    @Override
    @PostMapping("/sign-up")
    public CommonResponse<?> signUp(@RequestBody @Valid MemberRequest.SignUp signUp) {
        return memberService.signUp(signUp);
    }

    @Override
    @PostMapping("/sign-in")
    public CommonResponse<?> signIn(@RequestBody @Valid  MemberRequest.SignIn signIn, HttpServletRequest request,HttpServletResponse response) {
        return memberService.signIn(signIn,request,response);
    }

    @Override
    @DeleteMapping("/sign-out")
    public CommonResponse<?> signOut(@AuthenticationPrincipal CustomUserDetails userDetails, HttpServletRequest request) {
        return memberService.signOut(userDetails,request);
    }

    @Override
    @GetMapping("/withdraw")
    public CommonResponse<?> withdraw(@AuthenticationPrincipal CustomUserDetails userDetails, HttpServletRequest request) {
        return memberService.withdraw(userDetails,request);
    }

    @Override
    @PostMapping("/verify-password")
    public CommonResponse<?> checkVerificationPassword(@AuthenticationPrincipal CustomUserDetails userDetails,@RequestBody MemberRequest.VerificationPassword password) {
        return memberService.checkVerificationPassword(userDetails,password.password());
    }

    @Override
    @PutMapping("/change-nickname")
    public CommonResponse<?> changeNickname(@AuthenticationPrincipal CustomUserDetails userDetails,
                                            @RequestBody @Valid MemberRequest.CheckNickname nickname) {
        return memberService.changeNickname(userDetails, nickname);
    }

    @Override
    @PutMapping("/change-password")
    public CommonResponse<?> changePassword(@AuthenticationPrincipal CustomUserDetails userDetails,
                                            @RequestBody @Valid MemberRequest.CheckPassword password) {
        return memberService.changePassword(userDetails,password);
    }

    @Override
    @PutMapping("/change-message")
    public CommonResponse<?> changeMessage(@AuthenticationPrincipal CustomUserDetails userDetails,
                                           @RequestBody @Valid MemberRequest.CheckMessage message) {
        return memberService.changeMessage(userDetails,message);
    }

    @Override
    @PostMapping("/check-email")
    public CommonResponse<?> checkEmail(@RequestBody @Valid MemberRequest.CheckEmail request) {
        memberService.checkEmail(request.email());
        return CommonResponse.success();
    }

    @Override
    @PostMapping("/check-nickname")
    public CommonResponse<?> checkNickname(@RequestBody @Valid MemberRequest.CheckNickname request) {
        memberService.checkNickname(request.nickname());
        return CommonResponse.success();
    }

    @Override
    @PostMapping("/check-password")
    public CommonResponse<?> checkPassword(@RequestBody @Valid MemberRequest.CheckPassword request) {
        return CommonResponse.success();
    }



}
