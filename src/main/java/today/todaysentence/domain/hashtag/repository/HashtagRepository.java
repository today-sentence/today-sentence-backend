package today.todaysentence.domain.hashtag.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import today.todaysentence.domain.hashtag.Hashtag;

import java.util.Optional;

public interface HashtagRepository extends JpaRepository<Hashtag, Long> {

    Optional<Hashtag> findByName(String name);
}
