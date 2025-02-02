package today.todaysentence.domain.dailyquote;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import today.todaysentence.domain.post.Post;
import today.todaysentence.domain.user.User;

import java.time.LocalDate;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class DailyQuote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(name = "recipient_id")
    @ManyToOne(fetch = FetchType.LAZY)
    User recipient;

    @JoinColumn(name = "post_id")
    @ManyToOne(fetch = FetchType.LAZY)
    Post post;

    LocalDate createdAt;

    private DailyQuote(User recipient, Post post) {
        this.recipient = recipient;
        this.post = post;
        this.createdAt = LocalDate.now();
    }

    public static DailyQuote create(User recipient, Post post) {
        return new DailyQuote(recipient, post);
    }
}
