package today.todaysentence.domain.like.api;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import today.todaysentence.domain.like.service.LikeService;
import today.todaysentence.global.response.CommonResponse;
import today.todaysentence.global.security.userDetails.CustomUserDetails;
import today.todaysentence.global.swagger.LikeApiSpec;


@RestController
@RequestMapping("/api/likes")
@RequiredArgsConstructor
@ResponseStatus(HttpStatus.OK)    
public class LikeController implements LikeApiSpec {

    private final LikeService likeService;
    @Override
    @GetMapping("/{post-id}")
    public CommonResponse<?> likesToPost(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable("post-id")Long postId) {
        return likeService.toggleLikes(userDetails.member(), postId);
    }
}
