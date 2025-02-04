package today.todaysentence.domain.book.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import today.todaysentence.domain.book.Book;
import today.todaysentence.domain.book.repository.BookRepository;

@RequiredArgsConstructor
@Service
public class BookService {
    private final BookRepository bookRepository;

    public Book findOrCreate(Book book) {
        return bookRepository.findByIsbn(book.getIsbn())
                .orElseGet(() -> bookRepository.save(book));
    }
}
