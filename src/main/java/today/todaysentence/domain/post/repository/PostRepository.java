package today.todaysentence.domain.post.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import today.todaysentence.domain.post.Post;

public interface PostRepository extends JpaRepository<Post, Long> {
}
