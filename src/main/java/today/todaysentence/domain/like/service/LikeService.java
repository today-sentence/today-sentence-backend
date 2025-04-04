package today.todaysentence.domain.like.service;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import today.todaysentence.domain.like.Likes;
import today.todaysentence.domain.like.dto.LikeResponse;
import today.todaysentence.domain.like.repository.LikeRepository;
import today.todaysentence.domain.member.Member;
import today.todaysentence.domain.post.EventType;
import today.todaysentence.domain.post.dto.PostResponse;
import today.todaysentence.domain.post.dto.PostResponseDTO;
import today.todaysentence.domain.post.repository.PostRepository;
import today.todaysentence.global.redis.RedisService;
import today.todaysentence.global.response.CommonResponse;


@Service
@RequiredArgsConstructor
public class LikeService {

    private final LikeRepository likeRepository;
    private final RedisService redisService;
    private final ApplicationEventPublisher eventPublisher;



    @Transactional
    public CommonResponse<?> toggleLikes(Member member, Long postId) {

        Likes like = likeRepository.findByMemberAndPost(member.getId(), postId)
                .orElseGet(() -> createLike(member.getId(), postId));

        like.toggle();

        likeRepository.save(like);

        eventPublisher.publishEvent(new PostResponse.PostEventDto(postId, EventType.LIKE,like.getIsLiked()));

        return CommonResponse.ok(like.getIsLiked());
    }

    private Likes createLike(Long memberId, Long postId) {
        return likeRepository.save(
                Likes.builder()
                        .memberId(memberId)
                        .postId(postId)
                        .isLiked(false)
                        .build()
        );
    }

}
