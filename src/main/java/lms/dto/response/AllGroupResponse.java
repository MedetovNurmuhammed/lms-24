package lms.dto.response;

import lombok.Builder;

import java.util.List;

@Builder
public record AllGroupResponse(
        int page,
        int size,
        List<GroupResponse> groupResponses
) {
}
