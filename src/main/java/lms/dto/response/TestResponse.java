package lms.dto.response;

import lombok.Builder;

import java.util.List;

@Builder
public record TestResponse(
        String title,
        int hour,
        int minute,
        List<QuestionResponse> questionResponseList
) {
}
