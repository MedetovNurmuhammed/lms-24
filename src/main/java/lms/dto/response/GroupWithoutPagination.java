package lms.dto.response;

import lombok.Builder;

@Builder
public record GroupWithoutPagination(
        Long id,
        String groupName
) {
}
