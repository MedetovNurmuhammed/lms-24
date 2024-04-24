package lms.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lms.validation.phoneNumber.PhoneNumberValidation;

public record VideoRequest(
        @NotBlank
        @NotNull
        String titleOfVideo,
        @NotBlank
        @NotNull
        String description,
        @NotBlank
        @NotNull
        String linkOfVideo
) {
}
