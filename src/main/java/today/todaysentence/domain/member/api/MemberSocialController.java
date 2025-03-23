package today.todaysentence.domain.member.api;


import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import today.todaysentence.domain.member.SocialProvider;
import today.todaysentence.domain.member.dto.MemberResponse;
import today.todaysentence.domain.member.service.MemberSocialService;
import today.todaysentence.global.response.CommonResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/social")
public class MemberSocialController {

    private final MemberSocialService memberSocialService;

    @GetMapping("/kakao")
    public CommonResponse<MemberResponse.SocialSignupResponse> kakaoCallback(@RequestParam("accessToken") String accessToken, HttpServletRequest request, HttpServletResponse response) throws JsonProcessingException {

        MemberResponse.SocialSignupResponse memberInfo =
                memberSocialService.socialLogin(accessToken, SocialProvider.KAKAO, request, response);

        return CommonResponse.ok(memberInfo);
    }
}
