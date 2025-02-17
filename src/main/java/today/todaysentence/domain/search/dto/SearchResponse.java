package today.todaysentence.domain.search.dto;


import java.util.Set;

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
            String createAt,

            //like_count
            Long likesCount,
            Long bookmarkCount,
            Long commentCount
    ) {}

    public record HashTagRank(
            Set<String> search,
            Set<String> record

    ){

    }



}
