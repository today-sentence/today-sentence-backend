package today.todaysentence.domain.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import today.todaysentence.domain.user.User;

public interface UserRepository extends JpaRepository<User, Long> {
}
