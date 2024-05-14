package lms.dto.response;

import lms.enums.TaskAnswerStatus;
import lombok.Builder;
import java.util.List;

@Builder
public record AnswerTaskResponse(
        String text,
        String image,
        String file,
        TaskAnswerStatus taskAnswerStatus,
        int point,
        List<CommentResponse> comment
) {
}
