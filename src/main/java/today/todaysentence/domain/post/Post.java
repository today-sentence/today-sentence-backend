package today.todaysentence.domain.post;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import today.todaysentence.domain.book.Book;
import today.todaysentence.domain.category.Category;
import today.todaysentence.domain.hashtag.Hashtag;
import today.todaysentence.domain.user.User;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(name = "writer_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private User writer;

    @JoinColumn(name = "book_isbn")
    @ManyToOne(fetch = FetchType.LAZY)
    private Book book;

    @JoinColumn(name = "category_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Category category;

    @JoinTable(
            name = "post_hashtag",
            joinColumns = @JoinColumn(name = "post_id"),
            inverseJoinColumns = @JoinColumn(name = "hashtag_id")
    )

    @ManyToMany(fetch = FetchType.LAZY)
    private List<Hashtag> hashtags = new ArrayList<>();

    private String content;

    public Post(User writer, Book book, Category category, List<Hashtag> hashtags, String content) {

    }

}
