package today.todaysentence.domain.comment.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import today.todaysentence.domain.comment.Comment;
import today.todaysentence.domain.comment.dto.CommentRequest;
import today.todaysentence.domain.comment.dto.CommentResponse;
import today.todaysentence.domain.comment.repository.CommentRepository;
import today.todaysentence.domain.member.Member;
import today.todaysentence.domain.post.service.PostService;

@RequiredArgsConstructor
@Service
public class CommentService {
    private final PostService postService;
    private final CommentRepository commentRepository;

    @Transactional
    public void create(Member member, Long postId, CommentRequest.Create request) {
        postService.isValidPost(postId);

        commentRepository.save(new Comment(member, postId, request.content()));
    }

    public CommentResponse.CommentInfos getComments(Long postId, Pageable pageable) {
        postService.isValidPost(postId);

        Slice<Comment> comments = commentRepository.findByPostIdAndDeletedAtIsNull(postId, pageable);

        return new CommentResponse.CommentInfos(
                comments.stream()
                        .map(comment -> new CommentResponse.CommentInfo(
                                comment.getMember().getNickname(),
                                comment.getContent(),
                                comment.getCreateAt()
                        ))
                        .toList(),
                comments.getNumber(),
                comments.getNumberOfElements(),
                comments.hasNext()
        );

    }
}
