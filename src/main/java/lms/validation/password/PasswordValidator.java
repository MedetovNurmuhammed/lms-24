package lms.validation.password;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PasswordValidator implements ConstraintValidator<PasswordValidation, String> {
    @Override
    public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {
        log.error("Не корректный пароль!");
        return value.length() >= 6;
    }
}
