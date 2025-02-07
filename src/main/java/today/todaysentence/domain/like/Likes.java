package today.todaysentence.domain.like;


import jakarta.persistence.*;
import lombok.*;
import today.todaysentence.domain.member.Member;
import today.todaysentence.domain.post.Post;
import today.todaysentence.global.timeStamped.Timestamped;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class Likes extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(name = "member_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    @JoinColumn(name = "post_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Post post;

    @Builder.Default
    @Column(nullable = false)
    private Boolean isLiked =false;


    public void toggle() {
        this.isLiked = !this.isLiked;
    }
}
