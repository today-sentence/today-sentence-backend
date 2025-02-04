package today.todaysentence.domain.category;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum CategoryName {
    POEM_NOVEL_ESSAY("시/소설/에세이"),
    ECONOMY_MANAGEMENT("경제/경영"),
    HISTORY_SOCIETY("역사/사회"),
    PHILOSOPHY_PSYCHOLOGY("철학/심리학"),
    SELF_DEVELOPMENT("자기계발"),
    ARTS_PHYSICAL("예체능"),
    KID_YOUTH("아동/청소년"),
    TRAVEL_CULTURE("여행/문화"),
    ETC("기타");

    private final String koreanName;

    public static boolean isValid(String categoryName) {
        return Arrays.stream(CategoryName.values())
                .anyMatch(value -> value.koreanName.equals(categoryName));
    }

    public static CategoryName toCategoryName(String stringName) {
        return Arrays.stream(values())
                .filter(value -> value.koreanName.equals(stringName))
                .findFirst()
                .get();
    }
}
