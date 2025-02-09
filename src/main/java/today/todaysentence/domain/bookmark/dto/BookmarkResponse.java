package today.todaysentence.domain.bookmark.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

public class BookmarkResponse {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record SavedStatus(Boolean saved, Integer year, Integer month) {

        public static SavedStatus saved(Integer savedYear, Integer savedMonth) {
            return new SavedStatus(true, savedYear, savedMonth);
        }

        public static SavedStatus cancel() {
            return new SavedStatus(false, null, null);
        }
    }
}
