package today.todaysentence.domain.post.repository;

import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import today.todaysentence.domain.member.Member;
import today.todaysentence.domain.post.Post;
import today.todaysentence.domain.post.dto.PostResponse;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface PostRepository extends JpaRepository<Post, Long> {

    @Query("SELECT p FROM Post p WHERE p.writer = :member AND p.createAt BETWEEN :startDate AND :endDate")
    List<Post> findMyPostsByDate(@Param("member")Member member,
                                 @Param("startDate") LocalDateTime startDate,
                                 @Param("endDate") LocalDateTime endDate);

    Optional<Post> findById(@NonNull Long id);

    List<Post> findByWriter(Member member);

    boolean existsById(@NonNull Long id);

    @Query("SELECT new today.todaysentence.domain.post.dto.PostResponse$CategoryCount(" +
            "p.category, COUNT(*)" +
            ")  " +
            "FROM Post p " +
            "WHERE p.writer.id = :memberId " +
            "GROUP BY p.category")
    List<PostResponse.CategoryCount> findByMemberRecordsStatistics(@Param("memberId") Long id);

    @Query("SELECT new today.todaysentence.domain.post.dto.PostResponse$CategoryCount(" +
            "p.category, COUNT(*)" +
            ")  " +
            "FROM Post p " +
            "WHERE p.id IN " +
                "(SELECT b.postId " +
                "FROM Bookmark b " +
                "WHERE b.member.id = :memberId ) " +
            "GROUP BY p.category")
    List<PostResponse.CategoryCount> findByMemberBookmarksStatistics(@Param("memberId") Long id);

    @Query(value = """
      SELECT p.*
      FROM post p
      WHERE p.category = :category
      AND p.id NOT IN :duplicatedIds
      ORDER BY RAND()
      LIMIT :count
      """, nativeQuery = true)
    List<Post> findRandomPostsByCategoryAndNotInIds(String category, Set<Long> duplicatedIds, int count);


    @Query("SELECT new today.todaysentence.domain.post.dto.PostResponse$CategoryCount(" +
          "p.category,SUM(" +
          "                 CASE " +
          "                     WHEN p.writer.id = :memberId " +
          "                         THEN 1 " +
          "                         ELSE 0 " +
          "                 END +" +
          "                 CASE " +
          "                     WHEN p.id IN(SELECT b.postId FROM Bookmark b WHERE b.member.id = :memberId)" +
          "                          THEN 1 " +
          "                          ELSE 0 " +
          "                 END)" +
          ")" +
          "FROM Post p " +
          "GROUP BY p.category")
    List<PostResponse.CategoryCount> findByMemberAllStatistics(@Param("memberId") Long memberId);


    @Modifying
    @Query("UPDATE Post p SET p.deletedAt = CURRENT_TIMESTAMP WHERE p.id IN :postIds")
    void softDeleteByPostIds(@Param("postIds") List<Long> postIds);

    @Modifying
    @Query("DELETE FROM Post p WHERE p.deletedAt < :thirtyDays")
    int deletePostsBefore(@Param("thirtyDays") LocalDateTime thirtyDays);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @QueryHints(@QueryHint(name = "javax.persistence.lock.timeout", value = "5000"))
    @Query("SELECT p FROM Post p " +
            "JOIN FETCH p.writer m " +
            "JOIN FETCH p.hashtags h " +
            "WHERE p.id = :postId")
    Optional<Post> findByIdLock(@Param("postId") Long postId);
}










