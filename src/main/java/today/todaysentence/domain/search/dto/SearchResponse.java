package today.todaysentence.domain.search.dto;


public class SearchResponse {

    public record BookSearchResult(
            String bookTitle,
            String bookAuthor,
            String bookCover,
            String bookPublisher,
            Integer bookPublishingYear,
            Long postCount

    ){

    }

    public record PostSearchResult(
            //book
            String bookTitle,
            String bookAuthor,
            String bookCover,
            String bookPublisher,
            Integer bookPublishingYear,

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
