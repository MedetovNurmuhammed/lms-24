package lms.validation.phoneNumber;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PhoneNumberValidator implements ConstraintValidator<PhoneNumberValidation, String> {

    @Override
    public boolean isValid(String phoneNumber, ConstraintValidatorContext context) {
        if (phoneNumber == null) {
            return true;
        }

        boolean isValid = phoneNumber.startsWith("+") && phoneNumber.substring(1).matches("^[0-9]+$");

        if (!isValid) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                    "Номер телефона должен состоять только из цифр и символа '+'"
            ).addConstraintViolation();
            log.error("Неверный формат номера телефона: {}", phoneNumber);
        }

        return isValid;
    }
}

