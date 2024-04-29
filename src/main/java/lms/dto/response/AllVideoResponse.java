package lms.dto.response;

import lombok.Builder;
import java.util.List;

@Builder
public record AllVideoResponse(
        int page,
        int size,
        List<VideoResponse> videoResponses
) {
}
