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

    public record HashTagRank(
            Set<String> search,
            Set<String> record

    ){

    }



}
