package lms.dto.request;

import java.util.List;

public record AnswerTestRequest(
        List<Long>optionId
) {
}
