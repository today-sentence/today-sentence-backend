package today.todaysentence.domain.comment.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import today.todaysentence.domain.comment.Comment;
import today.todaysentence.domain.comment.dto.CommentRequest;
import today.todaysentence.domain.comment.dto.CommentResponse;
import today.todaysentence.domain.comment.repository.CommentRepository;
import today.todaysentence.domain.member.Member;
import today.todaysentence.domain.post.service.PostService;

import java.util.List;

@RequiredArgsConstructor
@Service
public class CommentService {
    private final PostService postService;
    private final CommentRepository commentRepository;

    @Transactional
    public void create(Member member, Long postId, CommentRequest.Create request) {
        postService.validatePost(postId);

        commentRepository.save(new Comment(member, postId, request.content()));
    }

    public List<CommentResponse.CommentInfo> getComments(Long postId) {
        postService.validatePost(postId);

        List<Comment> comments = commentRepository.findByPostIdOrderByCreateAtAsc(postId);

        return comments.stream()
                .map(comment -> new CommentResponse.CommentInfo(
                        comment.getMember().getNickname(),
                        comment.getContent(),
                        comment.getCreateAt()
                ))
                .toList();
    }
}
