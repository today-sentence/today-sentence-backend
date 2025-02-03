package today.todaysentence.domain.dailyquote.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import today.todaysentence.domain.dailyquote.DailyQuote;
import today.todaysentence.domain.post.Post;
import today.todaysentence.domain.post.service.PostService;
import today.todaysentence.domain.dailyquote.repository.DailyQuoteRepository;
import today.todaysentence.domain.member.Member;
import today.todaysentence.domain.member.service.MemberService;

import java.util.List;

@RequiredArgsConstructor
@Service
public class DailyQuoteService {
    private final MemberService memberService;
    private final PostService postService;
    private final DailyQuoteRepository dailyQuoteRepository;

    // TODO. 대규모 데이터 처리 시 -> Batch 처리 or 비동기 처리 고려
    @Transactional
    public void createDailyQuoteForAllUsers() {
        List<Member> members = memberService.findAll();

        for (Member recipient : members) {
            createDailyQuote(recipient);
        }
    }

    private void createDailyQuote(Member recipient) {
        List<Long> alreadyProvidedPostIds = dailyQuoteRepository.findAllIdsByUser(recipient);

        Post randomPost = postService.findRandomPostNotInProvided(recipient, alreadyProvidedPostIds);

        // 가져온 게시글 + 오늘 날짜 -> save(recommendation)
        DailyQuote dailyQuote = DailyQuote.create(recipient, randomPost);
        dailyQuoteRepository.save(dailyQuote);
    }
}
