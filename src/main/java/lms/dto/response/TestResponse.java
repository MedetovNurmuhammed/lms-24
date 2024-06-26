package lms.dto.response;

import lombok.Builder;

import java.util.List;

@Builder
public record TestResponse(
        Long testId,
        String title,
        int hour,
        int minute,
        List<QuestionResponse> questionResponseList
) {
}
