package today.todaysentence.domain.post.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import today.todaysentence.domain.post.dto.PostResponseDTO;
import today.todaysentence.domain.search.dto.SearchResponse;

import java.util.List;

@RequiredArgsConstructor
@Repository
public class PostRepositoryCustom {

    private final EntityManager entityManager;

    public List<SearchResponse.PostSearchResult> findPostsByDynamicQuery(String query) {
        String sql = "SELECT " +
                "b.title, b.author, b.cover, b.publisher, b.publishing_year, " +
                "p.id, m.nickname, p.content, p.category, " +
                "GROUP_CONCAT(DISTINCT h.name), " +
                "CAST(p.create_at AS CHAR) AS create_at, " +
                "COUNT(l.id) as like_count, " +
                "COUNT(bm.id) as bookmark_count, " +
                "COUNT(cm.id) as comment_count " +
                "FROM post p " +
                "INNER JOIN member m ON m.id = p.writer_id " +
                "INNER JOIN book b ON b.isbn = p.book_isbn " +
                "INNER JOIN post_hashtag ph ON ph.post_id = p.id " +
                "LEFT JOIN likes l ON l.post_id = p.id " +
                "LEFT JOIN bookmark bm ON bm.post_id = p.id " +
                "LEFT JOIN hashtag h ON h.id = ph.hashtag_id " +
                "LEFT JOIN comment cm ON cm.post_id = p.id " +
                "WHERE " + query + " " +
                "GROUP BY p.id " +
                "ORDER BY like_count DESC";

        Query nativeQuery = entityManager.createNativeQuery(sql, SearchResponse.PostSearchResult.class);

        return nativeQuery.getResultList();
    }

    public PostResponseDTO findPostByDynamicQuery(String query) {
        String sql = "SELECT " +
                "b.title, b.author, b.cover, b.publisher, b.publishing_year, " +
                "p.id, m.nickname, p.content, p.category, " +
                "GROUP_CONCAT(DISTINCT h.name), " +
                "CAST(p.create_at AS CHAR) AS create_at, " +
                "COUNT(l.id) as like_count, " +
                "COUNT(bm.id) as bookmark_count, " +
                "COUNT(cm.id) as comment_count " +
                "FROM post p " +
                "INNER JOIN member m ON m.id = p.writer_id " +
                "INNER JOIN book b ON b.isbn = p.book_isbn " +
                "INNER JOIN post_hashtag ph ON ph.post_id = p.id " +
                "LEFT JOIN likes l ON l.post_id = p.id " +
                "LEFT JOIN bookmark bm ON bm.post_id = p.id " +
                "LEFT JOIN hashtag h ON h.id = ph.hashtag_id " +
                "LEFT JOIN comment cm ON cm.post_id = p.id " +
                "WHERE " + query + " " +
                "GROUP BY p.id " +
                "ORDER BY like_count DESC";

        Query nativeQuery = entityManager.createNativeQuery(sql, PostResponseDTO.class);

        return (PostResponseDTO)nativeQuery.getSingleResult();
    }

    public PostResponseDTO findPostByNotMatchMember(String query) {
        String sql = "SELECT " +
                "b.title, b.author, b.cover, b.publisher, b.publishing_year, " +
                "p.id, m.nickname, p.content, p.category, " +
                "GROUP_CONCAT(DISTINCT h.name), " +
                "CAST(p.create_at AS CHAR) AS create_at, " +
                "COUNT(l.id) as like_count, " +
                "COUNT(bm.id) as bookmark_count, " +
                "COUNT(cm.id) as comment_count " +
                "FROM post p " +
                "INNER JOIN member m ON m.id = p.writer_id " +
                "INNER JOIN book b ON b.isbn = p.book_isbn " +
                "INNER JOIN post_hashtag ph ON ph.post_id = p.id " +
                "LEFT JOIN likes l ON l.post_id = p.id " +
                "LEFT JOIN bookmark bm ON bm.post_id = p.id " +
                "LEFT JOIN hashtag h ON h.id = ph.hashtag_id " +
                "LEFT JOIN comment cm ON cm.post_id = p.id " +
                "WHERE " + query + " " +
                "GROUP BY p.id " +
                "ORDER BY like_count DESC " +
                "limit 1";

        Query nativeQuery = entityManager.createNativeQuery(sql, PostResponseDTO.class);

        return (PostResponseDTO) nativeQuery.getSingleResult();
    }


}
