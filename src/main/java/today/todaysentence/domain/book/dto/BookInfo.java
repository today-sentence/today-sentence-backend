package today.todaysentence.domain.book.dto;

public record BookInfo(
        String title,
        String author,
        String cover,
        String publisher,
        Integer publishingYear
) {
}
