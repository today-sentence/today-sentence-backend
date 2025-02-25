package today.todaysentence.domain.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import today.todaysentence.domain.member.Member;
import today.todaysentence.domain.member.MemberUpdateAt;

import java.util.Optional;

public interface MemberUpdateAtRepository extends JpaRepository<MemberUpdateAt, Long> {

    Optional<MemberUpdateAt> findByMember(Member member);
}
