package today.todaysentence.domain.bookmark.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import today.todaysentence.domain.bookmark.Bookmark;
import today.todaysentence.domain.bookmark.dto.BookmarkResponse;
import today.todaysentence.domain.bookmark.repository.BookmarkRepository;
import today.todaysentence.domain.member.Member;
import today.todaysentence.domain.post.dto.PostResponse;
import today.todaysentence.domain.post.service.PostService;
import today.todaysentence.global.response.CommonResponse;

import java.util.List;

@RequiredArgsConstructor
@Service
public class BookmarkService {
    private final PostService postService;
    private final BookmarkRepository bookmarkRepository;

    @Transactional
    public BookmarkResponse.SavedStatus bookmark(Long postId, Member member) {
        postService.isValidPost(postId);

        Bookmark bookmark = bookmarkRepository.findByMemberAndPostId(member, postId)
                .orElseGet(() -> bookmarkRepository.save(new Bookmark(member, postId)));

        bookmark.toggle();

        if (bookmark.getIsSaved()) {
            return BookmarkResponse.SavedStatus.saved(bookmark.getBookmarkedYear(), bookmark.getBookmarkedMonth());
        }
        return BookmarkResponse.SavedStatus.cancel();
    }

    public List<PostResponse.Summary> getMyBookmarksByDate(Member member, int month, int year) {
        List<Bookmark> bookmarks = bookmarkRepository.findMyBookmarksByDate(member, month, year);

        return bookmarks.stream()
                .map(Bookmark::getPostId)
                .map(postService::toSummary)
                .toList();

    }
}
