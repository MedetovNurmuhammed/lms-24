package lms.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

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
