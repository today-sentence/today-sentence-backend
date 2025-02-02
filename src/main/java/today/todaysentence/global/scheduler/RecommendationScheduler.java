package today.todaysentence.global.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import today.todaysentence.domain.dailyquote.service.DailyQuoteService;

@Slf4j
@RequiredArgsConstructor
@Component
public class RecommendationScheduler {
    private final DailyQuoteService dailyQuoteService;

    @Scheduled(cron = "0 0 0 * * ?")
    public void createRecommendation() {
        dailyQuoteService.createDailyQuoteForAllUsers();
    }
}
