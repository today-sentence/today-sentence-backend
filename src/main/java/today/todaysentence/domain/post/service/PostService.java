package today.todaysentence.domain.post.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import today.todaysentence.domain.member.Member;
import today.todaysentence.domain.post.Post;
import today.todaysentence.domain.post.repository.PostQueryRepository;
import today.todaysentence.global.exception.exception.ExceptionCode;
import today.todaysentence.global.exception.exception.PostException;

import java.util.List;

@RequiredArgsConstructor
@Service
public class PostService {
    private final PostQueryRepository postQueryRepository;

    public Post findRandomPostNotInProvided(Member member, List<Long> recommendedPostIds) {
        return postQueryRepository.findOneNotInRecommended(member, recommendedPostIds)
                .orElseThrow(() -> new PostException(ExceptionCode.POST_NOT_FOUND));
    }
}
