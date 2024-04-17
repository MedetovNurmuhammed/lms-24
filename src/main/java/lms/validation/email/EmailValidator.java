package lms.validation.email;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EmailValidator implements ConstraintValidator<EmailValidation, String> {
    @Override
    public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {
        log.error("Не корректный почта!");
        return value.endsWith("@gmail.com");
    }
}
