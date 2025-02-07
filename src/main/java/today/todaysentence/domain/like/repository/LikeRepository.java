package today.todaysentence.domain.like.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import today.todaysentence.domain.like.Likes;
import today.todaysentence.domain.member.Member;
import today.todaysentence.domain.post.Post;

import java.util.Optional;

public interface LikeRepository extends JpaRepository<Likes,Long> {

    Optional<Likes> findByMemberAndPost(Member member, Post post);
}
