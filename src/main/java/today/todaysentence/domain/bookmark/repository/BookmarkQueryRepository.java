package today.todaysentence.domain.bookmark.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import today.todaysentence.domain.book.QBook;
import today.todaysentence.domain.bookmark.QBookmark;
import today.todaysentence.domain.bookmark.dto.BookmarkResponse;
import today.todaysentence.domain.member.Member;
import today.todaysentence.domain.post.Category;
import today.todaysentence.domain.post.QPost;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class BookmarkQueryRepository {
    private final JPAQueryFactory queryFactory;

    public BookmarkResponse.CategoryStatistics findBookmarksByCategory(Member member, Category category) {
        QBookmark bookmark = QBookmark.bookmark;
        QPost post = QPost.post;
        QBook book = QBook.book;

        List<BookmarkResponse.CategoryStatistic> statistics = queryFactory
                .select(Projections.constructor(BookmarkResponse.CategoryStatistic.class,
                        post.id,
                        book.title,
                        book.author,
                        bookmark.modifiedAt.month(),
                        bookmark.modifiedAt.dayOfMonth()
                ))
                .from(bookmark)
                .join(post).on(bookmark.postId.eq(post.id))
                .join(post.book, book)
                .where(
                        bookmark.member.eq(member),
                        post.category.eq(category),
                        post.deletedAt.isNull(),
                        bookmark.isSaved.isTrue()
                )
                .orderBy(bookmark.modifiedAt.desc())
                .fetch();

        Long totalCount = queryFactory
                .select(bookmark.id.count())
                .from(bookmark)
                .join(post).on(bookmark.postId.eq(post.id))
                .where(
                        bookmark.member.eq(member),
                        post.category.eq(category),
                        post.deletedAt.isNull(),
                        bookmark.isSaved.isTrue()
                )
                .fetchOne();

        return new BookmarkResponse.CategoryStatistics(
                totalCount,
                statistics
        );
    }
}
