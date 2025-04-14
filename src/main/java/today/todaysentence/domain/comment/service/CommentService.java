package today.todaysentence.domain.comment.service;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import today.todaysentence.domain.comment.Comment;
import today.todaysentence.domain.comment.dto.CommentRequest;
import today.todaysentence.domain.comment.dto.CommentResponse;
import today.todaysentence.domain.comment.repository.CommentRepository;
import today.todaysentence.domain.member.Member;
import today.todaysentence.domain.post.EventType;
import today.todaysentence.domain.post.Post;
import today.todaysentence.domain.post.dto.PostResponse;
import today.todaysentence.domain.post.service.PostService;
import today.todaysentence.global.exception.exception.CommentException;
import today.todaysentence.global.exception.exception.ExceptionCode;

@RequiredArgsConstructor
@Service
public class CommentService {
    private final PostService postService;
    private final CommentRepository commentRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public void create(Member member, Long postId, CommentRequest.Save request) {
        postService.isValidPost(postId);

        commentRepository.save(new Comment(member, postId, request.content()));
        eventPublisher.publishEvent(new PostResponse.PostEventDto(postId, EventType.COMMENT,true));
    }

    public CommentResponse.CommentInfos getComments(Long postId, Pageable pageable) {
        postService.isValidPost(postId);

        Slice<Comment> comments = commentRepository.findByPostIdAndDeletedAtIsNull(postId, pageable);

        return new CommentResponse.CommentInfos(
                comments.stream()
                        .map(comment -> new CommentResponse.CommentInfo(
                                comment.getId(),
                                comment.getMember().getNickname(),
                                comment.getContent(),
                                comment.getMember().getProfileImg(),
                                comment.getCreateAt()
                        ))
                        .toList(),
                comments.getNumber(),
                comments.getNumberOfElements(),
                comments.hasNext()
        );
    }

    @Transactional
    public void modify(Member member, Long postId, Long commentId, CommentRequest.Save request) {
        postService.isValidPost(postId);
        Comment comment = findComment(commentId);

        if (!comment.isWrittenBy(member)) {
            throw new CommentException(ExceptionCode.COMMENT_NOT_MATCHED_WRITER);
        }

        comment.update(request.content());
    }

    @Transactional
    public void delete(Member member, Long postId, Long commentId) {
        Post post = postService.findPost(postId);
        Comment comment = findComment(commentId);

        if (!comment.isWrittenBy(member)) {
            throw new CommentException(ExceptionCode.COMMENT_NOT_MATCHED_WRITER);
        }
        
        post.decrementCommentCount();
        comment.delete();
    }

    private Comment findComment(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentException(ExceptionCode.COMMENT_NOT_FOUND));
    }
}
