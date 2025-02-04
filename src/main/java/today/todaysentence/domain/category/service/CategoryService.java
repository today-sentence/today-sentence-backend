package today.todaysentence.domain.category.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import today.todaysentence.domain.category.Category;
import today.todaysentence.domain.category.CategoryName;
import today.todaysentence.domain.category.repository.CategoryRepository;

@RequiredArgsConstructor
@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;

    /**
     * ValidCategory 어노테이션을 통해
     * 카테고리의 유효성은 검증 완료되어 있음.
     * @param stringValue 문자열로 된 카테고리이름
     * @return 카테고리 문자열 이름에 해당하는 엔티티
     */
    public Category toCategory(String stringValue) {
        CategoryName categoryName = CategoryName.toCategoryName(stringValue);

        return categoryRepository.findByCategoryName(categoryName)
                .get();
    }
}
