package today.todaysentence.domain.bookmark.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import today.todaysentence.domain.bookmark.Bookmark;
import today.todaysentence.domain.member.Member;
import today.todaysentence.domain.post.Post;

import java.util.List;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {

    @Query("SELECT b FROM Bookmark b WHERE b.member = :member AND MONTH(b.createAt) = :month AND YEAR(b.createAt) = :year")
    List<Bookmark> findMyBookmarksByDate(@Param("member") Member member,
                                 @Param("month") int month,
                                 @Param("year") int year);
}
