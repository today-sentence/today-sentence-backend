package today.todaysentence.domain.post.api;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import today.todaysentence.domain.member.Member;
import today.todaysentence.domain.post.dto.PostRequest;
import today.todaysentence.domain.post.dto.PostResponse;
import today.todaysentence.domain.post.service.PostService;
import today.todaysentence.global.response.CommonResponse;
import today.todaysentence.global.security.userDetails.CustomUserDetails;
import today.todaysentence.global.swagger.PostApiSpec;

import java.util.List;

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

    @GetMapping("/records")
    public ResponseEntity<CommonResponse<List<PostResponse.Summary>>> getMyPostsByDate(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                                                      @RequestParam("month") int month,
                                                                                      @RequestParam("year") int year) {
        Member member = userDetails.member();
        List<PostResponse.Summary> posts = postService.getMyPostsByDate(member, month, year);

        if (posts.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(CommonResponse.ok(posts));
    }
}
