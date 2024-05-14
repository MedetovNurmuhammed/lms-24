package lms.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public record CheckAnswerRequest(
        @Min(0) @Max(10)
        int point,
        String comment,
        Boolean isAccept
) {
}
