package today.todaysentence.domain.category;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.Arrays;

public enum Category {
    POEM_NOVEL_ESSAY("시/소설/에세이"),
    ECONOMY_MANAGEMENT("경제/경영"),
    HISTORY_SOCIETY("역사/사회"),
    PHILOSOPHY_PSYCHOLOGY("철학/심리학"),
    SELF_DEVELOPMENT("자기계발"),
    ARTS_PHYSICAL("예체능"),
    KID_YOUTH("아동/청소년"),
    TRAVEL_CULTURE("여행/문화"),
    ETC("기타");

    private final String label;

    Category(String label) {
        this.label = label;
    }

    @JsonCreator
    public Category match(String request) {
        return Arrays.stream(values())
                .filter(value -> value.name().equalsIgnoreCase(request))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("잘못된 카테고리 값 : " + request));
    }
}
