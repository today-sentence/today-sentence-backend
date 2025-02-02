package today.todaysentence.domain.dailyquote.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.Param;
import today.todaysentence.domain.dailyquote.DailyQuote;
import today.todaysentence.domain.user.User;

import java.util.List;

@EnableJpaRepositories
public interface DailyQuoteRepository extends JpaRepository<DailyQuote, Long> {

    @Query("SELECT d.id FROM DailyQuote d WHERE d.recipient = :recipient")
    List<Long> findAllIdsByUser(@Param("recipient") User recipient);
}
