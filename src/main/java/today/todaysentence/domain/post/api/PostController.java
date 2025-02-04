package today.todaysentence.domain.post.api;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import today.todaysentence.domain.member.Member;
import today.todaysentence.domain.post.dto.PostRequest;
import today.todaysentence.domain.post.service.PostService;
import today.todaysentence.global.response.CommonResponse;
import today.todaysentence.global.security.userDetails.CustomUserDetails;
import today.todaysentence.global.swagger.PostApiSpec;

@RequiredArgsConstructor
@RequestMapping("/api/posts")
@RestController
public class PostController implements PostApiSpec {
    private final PostService postService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public CommonResponse<?> recordPost(@AuthenticationPrincipal CustomUserDetails userDetails, @Valid @RequestBody PostRequest.Record request) {
        Member member = userDetails.member();
        postService.record(request, member);
        return CommonResponse.success();
    }
}
