package lms.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lms.enums.StudyFormat;
import lms.validation.phoneNumber.PhoneNumberValidation;

public record StudentRequest(
        @NotBlank
        String firstName,
        @NotBlank
        String lastName,
        @PhoneNumberValidation
        String phoneNumber,
        @Email
        String email,
        @NotNull
        @NotBlank
        String groupName,
        StudyFormat studyFormat
) {
}
