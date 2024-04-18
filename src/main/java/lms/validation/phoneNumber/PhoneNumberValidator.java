package lms.validation.phoneNumber;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PhoneNumberValidator implements ConstraintValidator<PhoneNumberValidation, String> {
    @Override
    public boolean isValid(String phoneNumber, ConstraintValidatorContext constraintValidatorContext) {
        log.error("Не корректный тел номер!");
        return phoneNumber.startsWith("+996")
                && phoneNumber.length() == 13
                && phoneNumber.substring(4).matches("^[0-9]+$");
    }
}
