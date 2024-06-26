package lms.validation.email;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;


@Documented
@Constraint(validatedBy = {EmailValidator.class})
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR, ElementType.PARAMETER, ElementType.TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
public @interface EmailValidation {
    String message() default "{email must contains '@gmail.com' symbol}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
