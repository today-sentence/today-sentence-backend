package today.todaysentence.domain.post.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
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

    @Modifying
    @Query("DELETE FROM Post p WHERE p.deletedAt < :thirtyDays")
    int deletePostsDeletedBefore(@Param("thirtyDays") LocalDateTime thirtyDays);

    List<Post> findByWriter(Member member);

    @Query("SELECT p.id FROM Post p WHERE p.deletedAt < :oneMinuteAgo")
    List<Long> findPostIdsDeletedBefore(@Param("oneMinuteAgo") LocalDateTime oneMinuteAgo);

    @Modifying
    @Query("DELETE FROM Post p WHERE p.id IN :ids")
    void deleteByPostIds(@Param("ids") List<Long> ids);

    @Query("SELECT p.id FROM Post p WHERE p.writer.id IN :memberIds" )
    List<Long> findPostIdsDeleteToMemberIds(@Param("memberIds") List<Long> memberIds);


//    검색결과의 총 개수 혹시나 추후사용 가능성이 있어서 만들어만놓았어요
//    @Query(value = "SELECT COUNT(DISTINCT p.id) " +
//            "FROM post p " +
//            "INNER JOIN member m ON m.id = p.writer_id " +
//            "INNER JOIN book b ON b.isbn = p.book_isbn " +
//            "INNER JOIN post_hashtag ph ON ph.post_id = p.id " +
//            "LEFT JOIN likes l ON l.post_id = p.id " +
//            "LEFT JOIN hashtag h ON h.id = ph.hashtag_id " +
//            "WHERE b.title = :search",
//             nativeQuery = true)
//    Long countPostsByCategory(@Param("search") String search);

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


}










