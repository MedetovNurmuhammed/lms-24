package lms.dto.response;

import lombok.Builder;

import java.util.List;

@Builder
public record AllLinkResponse(
        int page,
        int size,
        List<LinkResponse> linkResponses
) {
}
