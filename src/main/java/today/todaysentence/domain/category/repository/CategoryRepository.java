package today.todaysentence.domain.category.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import today.todaysentence.domain.category.Category;
import today.todaysentence.domain.category.CategoryName;

import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    Optional<Category> findByCategoryName(CategoryName name);
}
