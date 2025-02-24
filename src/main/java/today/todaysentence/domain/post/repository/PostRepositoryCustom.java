package today.todaysentence.domain.post.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import today.todaysentence.domain.member.dto.InteractionResponseDTO;
import today.todaysentence.domain.post.dto.PostResponseDTO;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Repository
public class PostRepositoryCustom {

    private final EntityManager entityManager;

    public List<PostResponseDTO> findPostsByDynamicQuery(String search,String query,String orderBy, int size, int page) {

        String sql = "SELECT " +
                "b.title, b.author, b.cover, b.publisher, b.publishing_year, " +
                "p.id, m.nickname, p.content, p.category, " +
                "GROUP_CONCAT(DISTINCT h.name), " +
                "CAST(p.create_at AS CHAR) AS create_at, " +
                "COUNT(DISTINCT l.id) as like_count, " +
                "COUNT(DISTINCT bm.id) as bookmark_count, " +
                "COUNT(DISTINCT cm.id) as comment_count " +
                "FROM post p " +
                "INNER JOIN member m ON m.id = p.writer_id " +
                "INNER JOIN book b ON b.isbn = p.book_isbn " +
                "INNER JOIN post_hashtag ph ON ph.post_id = p.id " +
                "LEFT JOIN likes l ON l.post_id = p.id AND l.deleted_at IS NULL " +
                "LEFT JOIN bookmark bm ON bm.post_id = p.id AND bm.deleted_at IS NULL " +
                "LEFT JOIN hashtag h ON h.id = ph.hashtag_id " +
                "LEFT JOIN comment cm ON cm.post_id = p.id AND cm.deleted_at IS NULL " +
                "WHERE " + query + " AND p.deleted_at IS NULL " +
                "GROUP BY p.id " +
                "ORDER BY " + orderBy +
                "LIMIT :size OFFSET :offset";



        Query nativeQuery = entityManager.createNativeQuery(sql, PostResponseDTO.class);

        nativeQuery.setParameter("search", search);
        nativeQuery.setParameter("size", size);
        nativeQuery.setParameter("offset", page * size);

        return nativeQuery.getResultList();
    }

    public PostResponseDTO findPostByDynamicQuery(String query) {
        String sql = "SELECT " +
                "b.title, b.author, b.cover, b.publisher, b.publishing_year, " +
                "p.id, m.nickname, p.content, p.category, " +
                "GROUP_CONCAT(DISTINCT h.name), " +
                "CAST(p.create_at AS CHAR) AS create_at, " +
                "COUNT(DISTINCT l.id) as like_count, " +
                "COUNT(DISTINCT bm.id) as bookmark_count, " +
                "COUNT(DISTINCT cm.id) as comment_count " +
                "FROM post p " +
                "INNER JOIN member m ON m.id = p.writer_id " +
                "INNER JOIN book b ON b.isbn = p.book_isbn " +
                "INNER JOIN post_hashtag ph ON ph.post_id = p.id " +
                "LEFT JOIN likes l ON l.post_id = p.id AND l.deleted_at IS NULL " +
                "LEFT JOIN bookmark bm ON bm.post_id = p.id AND bm.deleted_at IS NULL " +
                "LEFT JOIN hashtag h ON h.id = ph.hashtag_id " +
                "LEFT JOIN comment cm ON cm.post_id = p.id AND cm.deleted_at IS NULL " +
                "WHERE " + query + " AND p.deleted_at IS NULL " +
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
                "COUNT(DISTINCT l.id) as like_count, " +
                "COUNT(DISTINCT bm.id) as bookmark_count, " +
                "COUNT(DISTINCT cm.id) as comment_count " +
                "FROM post p " +
                "INNER JOIN member m ON m.id = p.writer_id " +
                "INNER JOIN book b ON b.isbn = p.book_isbn " +
                "INNER JOIN post_hashtag ph ON ph.post_id = p.id " +
                "LEFT JOIN likes l ON l.post_id = p.id AND l.deleted_at IS NULL " +
                "LEFT JOIN bookmark bm ON bm.post_id = p.id AND bm.deleted_at IS NULL " +
                "LEFT JOIN hashtag h ON h.id = ph.hashtag_id " +
                "LEFT JOIN comment cm ON cm.post_id = p.id AND cm.deleted_at IS NULL " +
                "WHERE " + query + " " +
                "GROUP BY p.id " +
                "ORDER BY like_count DESC " +
                "limit 1";

        Query nativeQuery = entityManager.createNativeQuery(sql, PostResponseDTO.class);

        return (PostResponseDTO) nativeQuery.getSingleResult();
    }


    public List<InteractionResponseDTO> checkInteractions(List<Long> postIds, Long memberId) {

        String postIdsString = postIds.stream().map(String::valueOf).collect(Collectors.joining(","));

        String sql = "SELECT " +
                "COALESCE(MAX(l.is_liked), 0) AS is_liked, " +
                "COALESCE(MAX(bm.is_saved), 0) AS is_saved " +
                "FROM post p " +
                "LEFT JOIN likes l ON l.post_id = p.id AND l.member_id = :memberId " +
                "LEFT JOIN bookmark bm ON bm.post_id = p.id AND bm.member_id = :memberId " +
                "WHERE p.id IN :postIds " +
                "GROUP BY p.id " +
                "ORDER BY FIELD(p.id, " + postIdsString + ")";

        Query nativeQuery = entityManager.createNativeQuery(sql, InteractionResponseDTO.class);
        nativeQuery.setParameter("postIds", postIds);
        nativeQuery.setParameter("memberId", memberId);

        return nativeQuery.getResultList();
    }

    public InteractionResponseDTO checkInteraction(Long postId, Long memberId) {
        String sql = "SELECT " +
                "COALESCE(MAX(l.is_liked), 0) AS is_liked, " +
                "COALESCE(MAX(bm.is_saved), 0) AS is_saved " +
                "FROM post p " +
                "LEFT JOIN likes l ON l.post_id = p.id AND l.member_id = :memberId " +
                "LEFT JOIN bookmark bm ON bm.post_id = p.id AND bm.member_id = :memberId " +
                "WHERE p.id = :postId " +
                "GROUP BY p.id";

        Query nativeQuery = entityManager.createNativeQuery(sql, InteractionResponseDTO.class);
        nativeQuery.setParameter("postId", postId);
        nativeQuery.setParameter("memberId", memberId);

        return (InteractionResponseDTO)nativeQuery.getSingleResult();
    }

    public Long totalCount(String query, String search) {
        String countQuery = "SELECT COUNT(DISTINCT p.id) " +
                "FROM post p " +
                "INNER JOIN post_hashtag ph ON ph.post_id = p.id " +
                "INNER JOIN book b ON b.isbn = p.book_isbn " +
                "WHERE " + query + " AND p.deleted_at IS NULL";

        Query countNativeQuery = entityManager.createNativeQuery(countQuery);
        countNativeQuery.setParameter("search",search);

        return  (Long) countNativeQuery.getSingleResult();


    }
}
