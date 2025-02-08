package today.todaysentence.domain.post.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import today.todaysentence.domain.member.Member;
import today.todaysentence.domain.post.Post;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {

    @Query("SELECT p FROM Post p WHERE p.writer = :member AND p.createAt BETWEEN :startDate AND :endDate")
    List<Post> findMyPostsByDate(@Param("member")Member member,
                                 @Param("startDate") LocalDateTime startDate,
                                 @Param("endDate") LocalDateTime endDate);

    Optional<Post> findById(@NonNull Long id);

    boolean existsById(@NonNull Long id);
}
