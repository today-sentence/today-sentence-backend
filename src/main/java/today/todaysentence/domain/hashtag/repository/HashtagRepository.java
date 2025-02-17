package today.todaysentence.domain.hashtag.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import today.todaysentence.domain.hashtag.Hashtag;

import java.util.List;
import java.util.Optional;

public interface HashtagRepository extends JpaRepository<Hashtag, Long> {

    Optional<Hashtag> findByName(String name);

    @Query("SELECT h FROM Hashtag h")
    List<Hashtag> findAllName();


    @Query("SELECT h FROM Hashtag h")
    List<Hashtag> findAllIds();

    @Query("SELECT h FROM Hashtag h WHERE h.id NOT IN :hashIds")
    List<Hashtag> findNewIds(@Param("hashIds") List<Long> hashIdsLong);


}
