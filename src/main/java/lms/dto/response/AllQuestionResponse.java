package lms.dto.response;

import lombok.Builder;

import java.util.List;

@Builder
public record AllQuestionResponse(
        List<QuestionResponse>questionResponses
) {
}
