package lms.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lms.enums.QuestionType;

import java.util.List;

public record QuestionRequest(
        @NotNull
        String title,
        @Min(0)
        int point,
        QuestionType questionType
) {
}
