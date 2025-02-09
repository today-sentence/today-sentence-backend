package today.todaysentence.domain.member.repository;

import io.lettuce.core.dynamic.annotation.Param;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import today.todaysentence.domain.member.Member;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByEmail(@Param("email") String email);

    boolean existsByEmail(String email);

    boolean existsByNickname(String nickname);

    Optional<Member> findByNickname(String nickname);
}
