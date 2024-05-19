package lms.dto.request;

import jakarta.validation.constraints.NotNull;

public record OptionRequest(
        @NotNull
        String option,
        Boolean isTrue
) {
}
