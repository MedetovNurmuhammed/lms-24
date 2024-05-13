package lms.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lms.enums.QuestionType;

import java.util.List;

public record UpdateQuestionRequest(
        Long questionId,
        @NotNull
        String title,
        @Min(1)
        int point,
        QuestionType questionType,

        List<UpdateOptionRequest> updateOptionRequest
) {
}
