package today.todaysentence.util.scheduler;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import today.todaysentence.domain.dailyquote.service.DailyQuoteService;
import today.todaysentence.domain.like.repository.LikeRepository;
import today.todaysentence.domain.member.repository.MemberRepository;
import today.todaysentence.domain.post.repository.PostRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Slf4j
@RequiredArgsConstructor
@Component
public class RecommendationScheduler {

    private final DailyQuoteService dailyQuoteService;
    private final MemberRepository memberRepository;
    private final PostRepository postRepository;
    private final LikeRepository likeRepository;

    private final EntityManager entityManager;

    @Scheduled(cron = "0 0 0 * * ?")
    public void createRecommendation() {
        dailyQuoteService.createDailyQuoteForAllUsers();
    }

    @Scheduled(cron = "0 0 1 * * ?")
    @Transactional
    public void deleteUnlikedRecords() {
        log.info("unliked delete process start");

        int deletedCount = likeRepository.deleteIsLikeFalse();

        log.info("unliked delete process completed. Today deleted unliked: {}", deletedCount);
    }

    @Scheduled(cron = "0 0 2 * * ?")
    @Transactional
    public void hardDeleteSchedule(){

        log.info("Hard delete process start");

        LocalDateTime thirtyDays = LocalDateTime.now().minusDays(30);

        List<Long> memberIdsToDelete = memberRepository.findMemberIdsDeletedBefore(thirtyDays);
        //추후리플도추가
        List<Long> postIdsToDelete = postRepository.findPostIdsDeleteToMemberIds(memberIdsToDelete);
        Set<Long> likeIdsToDelete = likeRepository.findLikeIdsDeleteToPosts(postIdsToDelete);
        likeIdsToDelete.addAll(likeRepository.findByMemberId(memberIdsToDelete));

        if (memberIdsToDelete.isEmpty()) {
            log.info("No data deleted today.");
            return;
        }else{
            likeRepository.deleteByLikesIds(likeIdsToDelete);
            postRepository.deleteByPostIds(postIdsToDelete);
            memberRepository.deleteByMemberIds(memberIdsToDelete);
        }

        log.info("Hard delete process completed. Today deleted Members : {} , Posts : {}",memberIdsToDelete.size(),postIdsToDelete.size());
    }


}


