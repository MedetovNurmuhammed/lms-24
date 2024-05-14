package lms.dto.response;

import lombok.Builder;

@Builder
public record FilterAnswerOfTaskResponse(
        Long answerTasId,
        String studentName
) {
}
