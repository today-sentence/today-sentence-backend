package today.todaysentence.domain.search.dto;


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

    public record PostSearchResult(
            //book
            String bookTitle,
            String author,
            String coverUrl,
            String publisher,
            Integer publishingYear,

            //post
            Long postId,
            String postWriter,
            String postContent,
            String category,
            String hashtags,

            //like_count
            Long likesCount
    ) {}



}
