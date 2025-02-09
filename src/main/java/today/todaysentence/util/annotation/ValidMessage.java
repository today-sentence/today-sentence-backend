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
@Constraint(validatedBy = ValidMessage.MessageValidator.class)
public @interface ValidMessage {

    //정규식 바뀔 수 있음.
    String message() default "상태메시지는 1자이상 50자 이하여야합니다.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    class MessageValidator implements ConstraintValidator<ValidMessage, String> {

        @Override
        public boolean isValid(String message, ConstraintValidatorContext context) {
            if (message == null ) {
                return false;
            }

            return message.matches(".{1,50}");
        }
    }
}
