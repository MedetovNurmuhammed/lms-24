package lms.dto.response;

import lms.enums.QuestionType;

import java.util.List;

public record QuestionResponse(
        Long questionId,
        String title,
        double point,
        QuestionType questionType,
        List<OptionResponse> optionResponses
) {
}
