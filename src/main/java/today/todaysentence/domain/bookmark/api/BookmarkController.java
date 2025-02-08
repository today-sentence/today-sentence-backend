package today.todaysentence.domain.bookmark.api;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import today.todaysentence.domain.bookmark.dto.BookmarkRequest;
import today.todaysentence.domain.bookmark.service.BookmarkService;
import today.todaysentence.domain.member.Member;
import today.todaysentence.domain.post.dto.PostResponse;
import today.todaysentence.global.response.CommonResponse;
import today.todaysentence.global.security.userDetails.CustomUserDetails;
import today.todaysentence.global.swagger.BookmarkApiSpec;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class BookmarkController implements BookmarkApiSpec {
    private final BookmarkService bookmarkService;

    @PostMapping("/api/posts/bookmarks")
    public CommonResponse<?> bookmarkPost(@AuthenticationPrincipal CustomUserDetails userDetails, BookmarkRequest.Save request) {
        Member member = userDetails.member();

        return CommonResponse.ok(bookmarkService.bookmark(request.postId(), member));
    }

    @GetMapping("/api/posts/bookmarks")
    public ResponseEntity<CommonResponse<List<PostResponse.Summary>>> getMyBookmarksByDate(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                                                           @RequestParam("month") int month,
                                                                                           @RequestParam("year") int year) {
        Member member = userDetails.member();
        List<PostResponse.Summary> bookmarks = bookmarkService.getMyBookmarksByDate(member, month, year);

        if (bookmarks.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(CommonResponse.ok(bookmarks));
    }
}
