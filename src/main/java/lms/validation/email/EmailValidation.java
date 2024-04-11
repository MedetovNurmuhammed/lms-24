package lms.validation.email;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = {EmailValidator.class})
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR, ElementType.PARAMETER, ElementType.TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
public @interface EmailValidation {
    String message() default "{email must contains '@gmail.com' symbol}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
