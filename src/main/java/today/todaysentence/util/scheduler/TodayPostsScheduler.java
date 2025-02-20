package today.todaysentence.util.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import today.todaysentence.domain.member.service.MemberService;
import today.todaysentence.domain.post.dto.ScheduledPosts;
import today.todaysentence.domain.post.service.PostService;
import today.todaysentence.global.redis.RedisService;

import java.util.List;
import java.util.Set;

@Slf4j
@RequiredArgsConstructor
@Component
public class TodayPostsScheduler {
    private static final int POST_COUNT_FOR_EACH = 4;

    private final RedisService redisService;
    private final PostService postService;
    private final MemberService memberService;

    @Scheduled(cron = "0 30 2 * * ?")
    public void fetchAndCachePosts() {
        int resetMemberCount = memberService.initTodaySentence();

        Set<Long> duplicatedIds = redisService.getDuplicatedPostIds();

        List<ScheduledPosts> scheduledPosts = postService.fetchScheduledPostsByEachCategory(POST_COUNT_FOR_EACH, duplicatedIds);

        redisService.addScheduledPosts(scheduledPosts);

        log.info("resetMember : {} ",resetMemberCount);
    }
}
