package lms.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lms.enums.StudyFormat;
import lms.validation.email.EmailValidation;
import lms.validation.phoneNumber.PhoneNumberValidation;

public record StudentRequest(
        @NotBlank
        @NotNull
        String firstName,
        @NotBlank
        @NotNull
        String lastName,
        @PhoneNumberValidation
        String phoneNumber,
        @EmailValidation
        String email,
        @NotNull
        @NotBlank
        String groupName,
        @NotNull
        @NotBlank
        StudyFormat studyFormat
) {
}
