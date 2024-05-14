package lms.dto.response;

import lombok.Builder;

@Builder
public record OptionResponse(
        Long id,
        String option
) {
}
