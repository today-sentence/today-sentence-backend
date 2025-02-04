package today.todaysentence.global.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;
import today.todaysentence.domain.category.CategoryName;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({ElementType.FIELD})
@Retention(RUNTIME)
@Constraint(validatedBy = ValidCategory.CategoryValidator.class)
public @interface ValidCategory {
    String message() default "유효하지 않은 카테고리입니다.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

    class CategoryValidator implements ConstraintValidator<ValidCategory, String> {

        @Override
        public boolean isValid(String category, ConstraintValidatorContext constraintValidatorContext) {
            if (category == null || category.isBlank()) {
                return false;
            }

            return CategoryName.isValid(category);
        }
    }
}
