package today.todaysentence.domain.bookmark;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import today.todaysentence.domain.member.Member;
import today.todaysentence.global.timeStamped.Timestamped;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Bookmark extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(name = "member_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    @Column(name = "post_id")
    private Long postId;

    @Column(nullable = false)
    private Boolean isSaved;

    public Bookmark(Member member, Long postId) {
        this.member = member;
        this.postId = postId;
        isSaved = false;
    }

    public Integer getBookmarkedYear() {
        return this.getModifiedAt().getYear();
    }

    public Integer getBookmarkedMonth() {
        return this.getModifiedAt().getMonthValue();
    }

    public void toggle() {
        this.isSaved = !this.isSaved;
    }
}
