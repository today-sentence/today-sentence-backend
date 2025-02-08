package today.todaysentence.domain.search.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import today.todaysentence.domain.book.repository.BookRepository;
import today.todaysentence.domain.search.dto.SearchResponse;
import today.todaysentence.global.exception.exception.BaseException;
import today.todaysentence.global.exception.exception.ExceptionCode;
import today.todaysentence.global.response.CommonResponse;


@Service
@RequiredArgsConstructor
public class SearchService {

    private final BookRepository bookRepository;

    public CommonResponse<?> findBooks(String type, String search, Pageable pageable) {

        Page<SearchResponse.BookSearchResult> books;

        if ("title".equals(type)) {
            books = bookRepository.findByTitleContain(search, pageable);
        } else if ("author".equals(type)) {
            books = bookRepository.findByAuthorContain(search, pageable);
        } else {
            throw new BaseException(ExceptionCode.NOT_MATCHED_TYPE_PARAMETER);
        }
        if (books.isEmpty()) {
            return CommonResponse.ok("검색 결과가 없습니다.");
        }


        return CommonResponse.ok(books);
    }


}
