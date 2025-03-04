package today.todaysentence.domain.comment;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import today.todaysentence.domain.member.Member;
import today.todaysentence.global.timeStamped.Timestamped;

@Table(indexes = @Index(name = "idx_comment_post_id_create_at", columnList = "postId, createAt"))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Comment extends Timestamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(name = "member_id")
    @ManyToOne(fetch = FetchType.LAZY)
    Member member;

    Long postId;

    String content;

    public Comment(Member member, Long postId, String content) {
        this.member = member;
        this.postId = postId;
        this.content = content;
    }
}
