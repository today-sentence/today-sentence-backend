package today.todaysentence.domain.book.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import today.todaysentence.domain.book.Book;

import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, Long> {
    Optional<Book> findByIsbn(String isbn);
}
