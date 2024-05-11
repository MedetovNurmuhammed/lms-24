package lms.dto.request;

import lms.enums.TaskAnswerStatus;

public record AnswerTaskRequest(
        String text,
        String image,
        String file,
        String comment
) {
}
