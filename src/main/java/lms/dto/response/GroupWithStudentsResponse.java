package lms.dto.response;

import lombok.Builder;

@Builder
public record GroupWithStudentsResponse(
        Long id,
        String title
) {
}
