package today.todaysentence.global.anotation;

import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidPassword.PasswordValidator.class)
public @interface ValidPassword {
    String message() default "패스워드는 영문자, 숫자, 특수문자가 조합된 10자 이상 16자 이하여야 합니다.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

    class PasswordValidator implements ConstraintValidator<ValidPassword, String> {

        @Override
        public boolean isValid(String password, ConstraintValidatorContext context) {
            if (password == null) {
                return false;
            }

            return password.matches("^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{10,16}$");
        }
    }
}


