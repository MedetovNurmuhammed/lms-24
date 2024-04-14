package lms.dto.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record GroupRequest(
        @NotBlank
        String title,
        @NotBlank
        String description,
        @NotNull
        String image,
        @Future
        LocalDate dateOfEnd) {
}
