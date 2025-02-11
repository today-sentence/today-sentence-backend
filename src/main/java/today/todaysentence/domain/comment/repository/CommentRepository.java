package today.todaysentence.domain.comment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import today.todaysentence.domain.comment.Comment;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByPostIdOrderByCreateAtAsc(Long postId);
}
