package lms.dto.response;

import lms.enums.QuestionType;

import java.util.List;

public record QuestionResponse(
        String title,
        int point,
        QuestionType questionType,
        List<OptionResponse> optionResponses
) {
}
