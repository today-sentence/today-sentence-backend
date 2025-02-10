package today.todaysentence.domain.like.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import today.todaysentence.domain.like.Likes;
import today.todaysentence.domain.like.repository.LikeRepository;
import today.todaysentence.domain.member.Member;
import today.todaysentence.domain.post.Post;
import today.todaysentence.domain.post.repository.PostRepository;
import today.todaysentence.global.exception.exception.BaseException;
import today.todaysentence.global.exception.exception.ExceptionCode;
import today.todaysentence.global.response.CommonResponse;


@Service
@RequiredArgsConstructor
public class LikeService {

    private final LikeRepository likeRepository;
    private final PostRepository postRepository;

    @Transactional
    public CommonResponse<?> toggleLikes(Member member, Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new BaseException(ExceptionCode.POST_NOT_FOUND));

        Likes like = likeRepository.findByMemberAndPost(member, post)
                .orElseGet(() -> createLike(member, post));

        like.toggle();

        return CommonResponse.ok(like.getIsLiked());
    }

    private Likes createLike(Member member, Post post) {
        return likeRepository.save(
                Likes.builder()
                        .member(member)
                        .post(post)
                        .isLiked(false)
                        .build()
        );
    }


}
