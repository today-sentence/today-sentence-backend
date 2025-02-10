package today.todaysentence.domain.search.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import today.todaysentence.domain.book.repository.BookRepository;
import today.todaysentence.domain.post.repository.PostRepository;
import today.todaysentence.domain.search.dto.SearchResponse;
import today.todaysentence.global.exception.exception.BaseException;
import today.todaysentence.global.exception.exception.ExceptionCode;
import today.todaysentence.global.response.CommonResponse;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SearchService {

    private final BookRepository bookRepository;
    private final PostRepository postRepository;

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

    public CommonResponse<?> findPosts(String type, String search , Pageable pageable) {

        int limit = pageable.getPageSize();
        int offset = pageable.getPageNumber()*pageable.getPageSize();

        List<SearchResponse.PostSearchResult> posts;

        if ("title".equals(type)) {
            posts = postRepository.findPostsByBookTitle(search,limit,offset);
        } else if ("tag".equals(type)) {
           posts = postRepository.findPostsHashtag(search,limit,offset);
        }else if ("category".equals(type)) {
           posts = postRepository.findPostsCategory(search,limit,offset);
        }else{
            throw new BaseException(ExceptionCode.NOT_MATCHED_TYPE_PARAMETER);
        }
        if (posts.isEmpty()) {
            return CommonResponse.ok("검색 결과가 없습니다.");
        }


        return CommonResponse.ok(posts);
    }


}
