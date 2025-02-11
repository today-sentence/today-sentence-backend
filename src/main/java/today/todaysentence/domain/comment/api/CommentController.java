package today.todaysentence.domain.comment.api;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import today.todaysentence.domain.comment.dto.CommentRequest;
import today.todaysentence.domain.comment.dto.CommentResponse;
import today.todaysentence.domain.comment.service.CommentService;
import today.todaysentence.domain.member.Member;
import today.todaysentence.global.response.CommonResponse;
import today.todaysentence.global.security.userDetails.CustomUserDetails;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class CommentController {
    private final CommentService commentService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/posts/{post_id}/comments")
    public CommonResponse<?> createComment(@AuthenticationPrincipal CustomUserDetails userDetails,
                                           @PathVariable(name = "post_id") Long postId,
                                           @Valid @RequestBody CommentRequest.Create request) {
        Member member = userDetails.member();
        commentService.create(member, postId, request);

        return CommonResponse.success();
    }

    @GetMapping("/posts/{post_id}/comments")
    public CommonResponse<List<CommentResponse.CommentInfo>> getComments(@PathVariable(name = "post_id") Long postId) {
        return CommonResponse.ok(commentService.getComments(postId));
    }
}
