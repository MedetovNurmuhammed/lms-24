package lms.dto.response;

import lombok.Builder;

@Builder
public record LinkResponse(
        Long id,
        String title,
        String url
) {
}
