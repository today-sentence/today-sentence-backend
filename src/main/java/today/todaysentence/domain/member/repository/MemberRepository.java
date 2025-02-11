package today.todaysentence.domain.member.repository;

import io.lettuce.core.dynamic.annotation.Param;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import today.todaysentence.domain.member.Member;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByEmail(@Param("email") String email);

    boolean existsByEmail(String email);

    boolean existsByNickname(String nickname);

    Optional<Member> findByNickname(String nickname);


    @Query("SELECT m.id FROM Member m WHERE m.deletedAt < :thirtyDays")
    List<Long> findMemberIdsDeletedBefore(@Param("thirtyDays") LocalDateTime thirtyDays);

    @Modifying
    @Query("DELETE FROM Member m WHERE m.id IN :ids")
    void deleteByMemberIds(@Param("ids") List<Long> ids);

}
