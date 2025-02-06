package today.todaysentence.domain.post.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import today.todaysentence.domain.member.Member;
import today.todaysentence.domain.post.Post;

import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {

    @Query("SELECT p FROM Post p WHERE p.writer = :member AND MONTH(p.createAt) = :month AND YEAR(p.createAt) = :year")
    List<Post> findMyPostsByDate(@Param("member")Member member,
                                 @Param("month") int month,
                                 @Param("year") int year);

    Optional<Post> findById(@NonNull Long id);
}
