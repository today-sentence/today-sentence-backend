package today.todaysentence.domain.search.dto;

import today.todaysentence.domain.book.Book;
import today.todaysentence.domain.book.dto.BookInfo;
import today.todaysentence.domain.post.dto.PostResponse;

import java.util.List;

public class SearchResponse {

    public record BookSearchResult(
            String bookTitle,
            String author,
            String coverUrl,
            String publisher,
            Integer publishingYear,
            Long postCount

    ){

    }

}
