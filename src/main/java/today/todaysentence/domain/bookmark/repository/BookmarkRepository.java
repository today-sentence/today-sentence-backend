package today.todaysentence.domain.bookmark.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import today.todaysentence.domain.bookmark.Bookmark;
import today.todaysentence.domain.member.Member;
import today.todaysentence.domain.post.Post;

import java.util.List;
import java.util.Optional;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {

    @Query("SELECT b FROM Bookmark b WHERE b.member = :member AND MONTH(b.modifiedAt) = :month AND YEAR(b.modifiedAt) = :year")
    List<Bookmark> findMyBookmarksByDate(@Param("member") Member member,
                                 @Param("month") int month,
                                 @Param("year") int year);

    Optional<Bookmark> findByMemberAndPostId(Member member, Long postId);
}
