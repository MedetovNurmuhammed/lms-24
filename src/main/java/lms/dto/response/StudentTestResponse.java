package lms.dto.response;

import lombok.Builder;

@Builder
public record StudentTestResponse(
        Long resultTestId,
        String fullName,
        double point,
        Boolean isPassed
) {
}
