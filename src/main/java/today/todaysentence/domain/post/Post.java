package today.todaysentence.domain.post;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import today.todaysentence.domain.member.Member;
import today.todaysentence.domain.book.Book;
import today.todaysentence.domain.category.Category;
import today.todaysentence.domain.hashtag.Hashtag;
import today.todaysentence.global.timeStamped.Timestamped;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(
        indexes = @Index(name = "idx_category", columnList = "category") // 단일 인덱스 적용
)
public class Post extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(name = "writer_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Member writer;

    @JoinColumn(name = "book_isbn")
    @ManyToOne(fetch = FetchType.LAZY)
    private Book book;

    @Enumerated(EnumType.STRING)
    private Category category;

    @JoinTable(
            name = "post_hashtag",
            joinColumns = @JoinColumn(name = "post_id"),
            inverseJoinColumns = @JoinColumn(name = "hashtag_id")
    )
    @ManyToMany(fetch = FetchType.LAZY)
    private List<Hashtag> hashtags = new ArrayList<>();

    private String content;

    public Post(Member writer, Book book, Category category, List<Hashtag> hashtags, String content) {
        this.writer = writer;
        this.book = book;
        this.category = category;
        this.hashtags = hashtags;
        this.content = content;
    }

    public List<String> getHashtagNames() {
        return hashtags.stream()
                .map(Hashtag::getName)
                .toList();
    }
}
