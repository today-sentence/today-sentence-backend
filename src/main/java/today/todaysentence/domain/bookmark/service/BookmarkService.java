package today.todaysentence.domain.bookmark.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import today.todaysentence.domain.bookmark.Bookmark;
import today.todaysentence.domain.bookmark.repository.BookmarkRepository;
import today.todaysentence.domain.member.Member;
import today.todaysentence.domain.post.dto.PostResponse;
import today.todaysentence.domain.post.service.PostService;

import java.util.List;

@RequiredArgsConstructor
@Service
public class BookmarkService {
    private final PostService postService;
    private final BookmarkRepository bookmarkRepository;

    @Transactional
    public void bookmark(Long postId, Member member) {
        bookmarkRepository.save(new Bookmark(member, postId));
    }

    public List<PostResponse.Summary> getMyBookmarksByDate(Member member, int month, int year) {
        List<Bookmark> bookmarks = bookmarkRepository.findMyBookmarksByDate(member, month, year);

        return bookmarks.stream()
                .map(Bookmark::getPostId)
                .map(postService::toSummary)
                .toList();

    }
}
