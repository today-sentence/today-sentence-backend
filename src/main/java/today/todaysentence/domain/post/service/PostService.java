package today.todaysentence.domain.post.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import today.todaysentence.domain.post.Post;
import today.todaysentence.domain.post.repository.PostQueryRepository;
import today.todaysentence.domain.user.User;
import today.todaysentence.global.exception.exception.ExceptionCode;
import today.todaysentence.global.exception.exception.PostException;

import java.util.List;

@RequiredArgsConstructor
@Service
public class PostService {
    private final PostQueryRepository postQueryRepository;

    public Post findRandomPostNotInProvided(User user, List<Long> recommendedPostIds) {
        return postQueryRepository.findOneNotInRecommended(user, recommendedPostIds)
                .orElseThrow(() -> new PostException(ExceptionCode.POST_NOT_FOUND));
    }
}
