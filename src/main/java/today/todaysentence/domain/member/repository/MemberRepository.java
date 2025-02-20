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
import java.util.Set;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByEmail(@Param("email") String email);

    boolean existsByEmail(String email);

    boolean existsByNickname(String nickname);

    Optional<Member> findByNickname(String nickname);


    @Query("SELECT m.id FROM Member m WHERE m.deletedAt < :thirtyDays")
    Set<Long> findMemberIdsDeletedBefore(@Param("thirtyDays") LocalDateTime thirtyDays);

    @Query("SELECT m FROM Member m WHERE m.todayPostId IN :postIds")
    List<Member> findByPostIds(@Param("postIds")Set<Long> postIds);

    @Modifying
    @Query("UPDATE Member m SET m.todayPostId = NULL WHERE m.todayPostId IS NOT NULL")
    int initTodaySentence();

}
