package today.todaysentence.domain.comment.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import today.todaysentence.domain.comment.Comment;

import java.time.LocalDateTime;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    Slice<Comment> findByPostIdAndDeletedAtIsNull(Long postId, Pageable pageable);


    @Modifying
    @Query("UPDATE Comment c SET c.deletedAt = CURRENT_TIMESTAMP WHERE c.member.id = :memberId")
    int softDeleteCommentByMember(@Param("memberId") Long memberId);

    @Modifying
    @Query("DELETE FROM Comment c WHERE c.deletedAt < :thirtyDays")
    void deleteCommentBefore(@Param("thirtyDays") LocalDateTime thirtyDays);
}
