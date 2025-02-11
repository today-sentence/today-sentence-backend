package today.todaysentence.domain.like.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import today.todaysentence.domain.like.Likes;
import today.todaysentence.domain.member.Member;
import today.todaysentence.domain.post.Post;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface LikeRepository extends JpaRepository<Likes,Long> {

    Optional<Likes> findByMemberAndPost(Member member, Post post);




    @Modifying
    @Query("DELETE FROM Likes l WHERE l.id IN :ids")
    void deleteByLikesIds(@Param("ids") Set<Long> ids);

    @Query("SELECT l.id FROM Likes l WHERE l.post.id IN :postsIds")
    Set<Long> findLikeIdsDeleteToPosts(@Param("postsIds") List<Long> postsIds);

    @Query("SELECT l.id FROM Likes l WHERE l.member.id IN :memberIds")
    Set<Long> findByMemberId(@Param("memberIds")List<Long> memberIds);

    @Modifying
    @Query("DELETE FROM Likes l WHERE l.isLiked = false")
    int deleteIsLikeFalse();
}
