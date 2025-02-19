package today.todaysentence.domain.bookmark.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import today.todaysentence.domain.bookmark.Bookmark;
import today.todaysentence.domain.member.Member;
import today.todaysentence.domain.post.Post;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {

    @Query("SELECT b FROM Bookmark b WHERE b.member = :member AND MONTH(b.modifiedAt) = :month AND YEAR(b.modifiedAt) = :year")
    List<Bookmark> findMyBookmarksByDate(@Param("member") Member member,
                                 @Param("month") int month,
                                 @Param("year") int year);

    Optional<Bookmark> findByMemberAndPostId(Member member, Long postId);

    @Modifying
    @Query("DELETE FROM Bookmark b WHERE b.isSaved = false")
    int deleteIsSavedFalse();

    @Modifying
    @Query("UPDATE Bookmark bm SET bm.deletedAt = CURRENT_TIMESTAMP WHERE bm.member.id =:memberId")
    int softDeleteBookmarkLikeByMember(@Param("memberId") Long memberId);

    @Modifying
    @Query("DELETE FROM Bookmark bm WHERE bm.deletedAt < :thirtyDays")
    void deleteBookmarksBefore(@Param("thirtyDays") LocalDateTime thirtyDays);
}
