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

@Tag(name = "유저")
@RestController
@RequestMapping("/api/member")
@RequiredArgsConstructor
@Slf4j
public class MemberController implements MemberApiSpec {

    private final MemberService memberService;

    @GetMapping("/auth")
    public void testMe(@AuthenticationPrincipal CustomUserDetails userDetails){
      log.info("userDetails : {}",userDetails);
    }

    @Override
    @PostMapping("/signUp")
    public CommonResponse<?> signUp(@RequestBody @Valid MemberRequest.SignUp signUp) {
        return memberService.signUp(signUp);
    }

    @Override
    @PostMapping("/signIn")
    public CommonResponse<?> signIn(@RequestBody @Valid  MemberRequest.SignIn signIn, HttpServletRequest request,HttpServletResponse response) {
        return memberService.signIn(signIn,request,response);
    }

    @Override
    @DeleteMapping("/signOut")
    public CommonResponse<?> signOut(@AuthenticationPrincipal CustomUserDetails userDetails, HttpServletRequest request) {
        return memberService.signOut(userDetails,request);
    }

    @Override
    @PostMapping("/checkEmail")
    public CommonResponse<?> checkEmail(@RequestBody @Valid MemberRequest.CheckEmail request) {
        memberService.checkEmail(request.email());
        return CommonResponse.success();
    }

    @Override
    @PostMapping("/checkNickname")
    public CommonResponse<?> checkNickname(MemberRequest.CheckNickname request) {
        memberService.checkNickname(request.nickname());
        return CommonResponse.success();
    }

    @Override
    @PostMapping("/checkPassword")
    public CommonResponse<?> checkPassword(@RequestBody @Valid MemberRequest.CheckPassword request) {
        return CommonResponse.success();
    }



}
