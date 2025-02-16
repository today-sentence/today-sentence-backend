package today.todaysentence.domain.post.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import today.todaysentence.domain.member.Member;
import today.todaysentence.domain.post.Post;
import today.todaysentence.domain.post.dto.PostResponse;
import today.todaysentence.domain.search.dto.SearchResponse;

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

    @Query(value = "SELECT " +
            "b.title , b.author , b.cover, b.publisher, b.publishing_year, " +
            "p.id , m.nickname, p.content, p.category , " +
            "GROUP_CONCAT(DISTINCT h.name), " +
            "CAST(p.create_at AS CHAR) AS create_at, " +
            "COUNT(l.id) as like_count, " +
            "COUNT(bm.id) as bookmark_count " +
            "FROM post p " +
            "INNER JOIN member m ON m.id = p.writer_id " +
            "INNER JOIN book b ON b.isbn = p.book_isbn " +
            "INNER JOIN post_hashtag ph ON ph.post_id = p.id " +
            "LEFT JOIN likes l ON l.post_id = p.id " +
            "LEFT JOIN bookmark bm ON bm.post_id = p.id " +
            "LEFT JOIN hashtag h ON h.id = ph.hashtag_id " +
            "WHERE b.title = :search " +
            "GROUP BY p.id " +
            "ORDER BY like_count DESC " +
            "LIMIT :limit OFFSET :offset", nativeQuery = true)
    List<SearchResponse.PostSearchResult> findPostsByBookTitle(
            @Param("search") String search,
            @Param("limit") int limit,
            @Param("offset") int offset);


    @Query(value = "SELECT " +
            "b.title, b.author, b.cover, b.publisher, b.publishing_year, " +
            "p.id, m.nickname, p.content, p.category, " +
            "GROUP_CONCAT(DISTINCT h.name) AS hashtags," +
            "CAST(p.create_at AS CHAR) AS create_at, " +
            "COUNT(l.id) AS like_count, " +
            "COUNT(bm.id) AS bookmark_count " +
            "FROM post p " +
            "INNER JOIN member m ON m.id = p.writer_id " +
            "INNER JOIN book b ON b.isbn = p.book_isbn " +
            "INNER JOIN post_hashtag ph ON ph.post_id = p.id " +
            "LEFT JOIN likes l ON l.post_id = p.id " +
            "LEFT JOIN bookmark bm ON bm.post_id = p.id " +
            "LEFT JOIN hashtag h ON h.id = ph.hashtag_id " +
            "WHERE ph.post_id IN (" +
            "  SELECT ph.post_id " +
            "  FROM post_hashtag ph " +
            "  INNER JOIN hashtag h ON h.id = ph.hashtag_id " +
            "  WHERE h.name LIKE :search" +
            ") " +
            "GROUP BY p.id " +
            "ORDER BY like_count DESC " +
            "LIMIT :limit OFFSET :offset", nativeQuery = true)
    List<SearchResponse.PostSearchResult> findPostsHashtag(@Param("search") String search,
                                                           @Param("limit") int limit,
                                                           @Param("offset") int offset);

    @Query(value = "SELECT " +
            "b.title , b.author , b.cover, b.publisher, b.publishing_year, " +
            "p.id , m.nickname, p.content, p.category , " +
            "GROUP_CONCAT(DISTINCT h.name), " +
            "CAST(p.create_at AS CHAR) AS create_at, " +
            "COUNT(l.id) as like_count, " +
            "COUNT(bm.id) AS bookmark_count " +
            "FROM post p " +
            "INNER JOIN member m ON m.id = p.writer_id " +
            "INNER JOIN book b ON b.isbn = p.book_isbn " +
            "INNER JOIN post_hashtag ph ON ph.post_id = p.id " +
            "LEFT JOIN likes l ON l.post_id = p.id " +
            "LEFT JOIN bookmark bm ON bm.post_id = p.id " +
            "LEFT JOIN hashtag h ON h.id = ph.hashtag_id " +
            "WHERE p.category = :search " +
            "GROUP BY p.id " +
            "ORDER BY like_count DESC " +
            "LIMIT :limit OFFSET :offset", nativeQuery = true)
    List<SearchResponse.PostSearchResult> findPostsCategory(@Param("search") String search,
                                                           @Param("limit") int limit,
                                                           @Param("offset") int offset);

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
      FROM Post p
      WHERE p.category = :category
      AND p.id NOT IN :duplicatedIds
      ORDER BY RAND()
      LIMIT :count
      """, nativeQuery = true)
    List<Post> findRandomPostsByCategoryAndNotInIds(String category, Set<Long> duplicatedIds, int count);
           }
