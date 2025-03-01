package today.todaysentence.domain.like.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import today.todaysentence.domain.like.Likes;
import today.todaysentence.domain.member.Member;
import today.todaysentence.domain.post.Post;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface LikeRepository extends JpaRepository<Likes,Long> {

    @Query("SELECT l FROM Likes l WHERE l.memberId = :memberId AND l.postId = :postId")
    Optional<Likes> findByMemberAndPost(@Param("memberId") Long memberId,@Param("postId") Long postId);

    @Modifying
    @Query("DELETE FROM Likes l WHERE l.isLiked = false")
    int deleteIsLikeFalse();

    @Modifying
    @Query("UPDATE Likes l SET l.isLiked = false WHERE l.memberId = :memberId")
    int softDeleteLikeByMember(@Param("memberId") Long memberId);


}
