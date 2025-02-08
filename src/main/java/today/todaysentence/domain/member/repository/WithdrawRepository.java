package today.todaysentence.domain.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import today.todaysentence.domain.member.WithdrawMember;

import java.util.Optional;


public interface WithdrawRepository extends JpaRepository<WithdrawMember,Long> {


    Optional<WithdrawMember> findByEmail(String email);
}
