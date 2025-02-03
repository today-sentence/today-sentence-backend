package today.todaysentence.domain.post.repository;

import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import today.todaysentence.domain.post.Post;
import today.todaysentence.domain.post.QPost;
import today.todaysentence.domain.member.Member;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

@RequiredArgsConstructor
@Repository
public class PostQueryRepository {
    private final JPAQueryFactory queryFactory;

    /**
     * <p>최소/최대 ID 범위 내에서 랜덤 ID 생성:
     * minId부터 maxId까지 범위 내에서 랜덤 ID를 생성하여 조회.</p>
     * <p>1. 최대 5번까지 랜덤 조회 시도:
     * 만약 조건에 맞는 게시글이 없다면 최대 5번까지 새로운 랜덤 ID로 재시도.
     * 이렇게 하면 랜덤성이 유지되면서도 조회 실패 확률을 줄일 수 있음.</p>
     * 2. 백업 플랜 (Fallback) 실행:
     * 모든 시도가 실패하면, 조건에 맞는 게시글 중 랜덤하게 하나를 조회하는 백업 로직 수행.
     * 여기서 ORDER BY RAND()를 사용하는 이유는 마지막 수단으로서 데이터 정확성을 보장하기 위함.
     *
     * @param member
     * @param recommendedPostIds 이미 추천된 명언 게시글들의 id 리스트
     * @return
     */
    public Optional<Post> findOneNotInRecommended(Member member, List<Long> recommendedPostIds) {
        QPost post = QPost.post;

        Long minId = queryFactory.select(post.id.min()).from(post).fetchOne();
        Long maxId = queryFactory.select(post.id.max()).from(post).fetchOne();

        if (minId == null || maxId == null) {
            return Optional.empty();
        }

        for (int i = 0; i < 5; i++) {
            long randomId = ThreadLocalRandom.current().nextLong(minId, maxId + 1);

            Post randomPost = queryFactory
                    .selectFrom(post)
                    .where(
                            post.id.goe(randomId),
                            post.writer.ne(member),
                            post.id.notIn(recommendedPostIds)
                    )
                    .fetchFirst();

            if (randomPost != null) {
                return Optional.of(randomPost);
            }
        }

        Post fallbackPost = queryFactory
                .selectFrom(post)
                .where(
                        post.writer.ne(member),
                        post.id.notIn(recommendedPostIds)
                )
                .orderBy(Expressions.numberTemplate(Double.class, "RAND()").asc()) // "RAND()" 는 데이터베이스 문법에 종속적임. 인터페이스화 필요
                .fetchFirst();

        return Optional.ofNullable(fallbackPost);
    }
}
