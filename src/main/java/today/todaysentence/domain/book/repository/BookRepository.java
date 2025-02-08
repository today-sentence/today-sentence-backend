package today.todaysentence.domain.book.repository;

import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import today.todaysentence.domain.book.Book;
import today.todaysentence.domain.search.dto.SearchResponse;

import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, Long> {
    Optional<Book> findByIsbn(String isbn);



    @Query(
           "SELECT new today.todaysentence.domain.search.dto.SearchResponse$BookSearchResult(" +
                   "b.title,b.author,b.cover,b.publisher,b.publishingYear , COUNT(p.id)" +
                   ") " +
                   "FROM Book b " +
                   "INNER JOIN Post p ON p.book = b " +
                   "WHERE b.title LIKE %:search% " +
                   "GROUP BY b " +
                   "ORDER BY COUNT(p.id) DESC"
    )
    Page<SearchResponse.BookSearchResult> findByTitleContain( @Param("search") String search, Pageable pageable);

    @Query(
            "SELECT new today.todaysentence.domain.search.dto.SearchResponse$BookSearchResult(" +
                    "b.title,b.author,b.cover,b.publisher,b.publishingYear , COUNT(p.id)" +
                    ") " +
                    "FROM Book b " +
                    "INNER JOIN Post p ON p.book = b " +
                    "WHERE  b.author LIKE %:search% " +
                    "GROUP BY b " +
                    "ORDER BY COUNT(p.id) DESC"
    )
    Page<SearchResponse.BookSearchResult> findByAuthorContain( @Param("search") String search, Pageable pageable);



}
