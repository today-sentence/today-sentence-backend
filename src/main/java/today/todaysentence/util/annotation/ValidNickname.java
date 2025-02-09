package today.todaysentence.util.annotation;


import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidNickname.NicknameValidator.class)
public @interface ValidNickname {

    //정규식 바뀔 수 있음.
    String message() default "닉네임은  한글, 특수문자, 영문 으로 가능하며 1자이상 8자 이하여야합니다.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    class NicknameValidator implements ConstraintValidator<ValidNickname, String> {

        @Override
        public boolean isValid(String message, ConstraintValidatorContext context) {
            if (message == null) {
                return false;
            }

            return message.matches("^[a-zA-Z0-9가-힣!@#$%^&*()_+=-]{1,8}$");
        }
    }
}