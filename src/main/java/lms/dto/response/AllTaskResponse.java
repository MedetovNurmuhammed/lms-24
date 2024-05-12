package lms.dto.response;

import lombok.Builder;
import java.util.List;

@Builder
public record AllTaskResponse(
        int page,
        int size,
        List<TaskResponse> taskResponse
) {
}
