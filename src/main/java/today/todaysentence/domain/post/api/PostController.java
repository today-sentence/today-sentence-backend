package today.todaysentence.domain.post.api;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import today.todaysentence.domain.member.Member;
import today.todaysentence.domain.member.repository.MemberRepository;
import today.todaysentence.domain.member.service.MemberService;
import today.todaysentence.domain.post.dto.PostRequest;
import today.todaysentence.domain.post.dto.PostResponse;
import today.todaysentence.domain.post.dto.PostResponseDTO;
import today.todaysentence.domain.post.repository.PostRepositoryCustom;
import today.todaysentence.domain.post.service.PostService;
import today.todaysentence.global.response.CommonResponse;
import today.todaysentence.global.security.userDetails.CustomUserDetails;
import today.todaysentence.global.security.userDetails.JwtUserDetails;
import today.todaysentence.global.swagger.PostApiSpec;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/api/posts")
@RestController
public class PostController implements PostApiSpec {
    private final PostService postService;
    
    //테스트용으로 실배포시 삭제
    private final PostRepositoryCustom postRepositoryCustom;
    private final MemberService memberService;

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

    @GetMapping("/{post_id}")
    public CommonResponse<PostResponse.Detail> getPostDetail(@PathVariable("post_id") Long postId) {
        return CommonResponse.ok(postService.getPostDetail(postId));
    }

    @GetMapping("/test/{post_id}")
    public CommonResponse<?> test(@AuthenticationPrincipal JwtUserDetails userDetails,
            @PathVariable("post_id") Long postId) {

        String query = "p.id = " + postId;
        PostResponseDTO result = postRepositoryCustom.findPostByDynamicQuery(query);
        PostResponse.PostResult testDto = new PostResponse.PostResult(result,memberService.checkInteraction(postId, userDetails.id()));
        return CommonResponse.ok(testDto);
    }

    @GetMapping("/statistics")
    public CommonResponse<PostResponse.Statistics> getStatistics(@AuthenticationPrincipal JwtUserDetails userDetails){
        return postService.getStatistics(userDetails);
    }

    @GetMapping("/today-sentence")
    public CommonResponse<PostResponse.PostResult> getTodaySentence(@AuthenticationPrincipal CustomUserDetails userDetails){
        return postService.getTodaySentence(userDetails);
    }



}
