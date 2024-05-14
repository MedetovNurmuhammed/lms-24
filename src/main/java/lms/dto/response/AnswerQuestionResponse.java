package lms.dto.response;

import com.amazonaws.services.wellarchitected.model.QuestionType;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class AnswerQuestionResponse {
    private Long questionId;
    private String questionTitle;
    private QuestionType questionType;
    private List<AnswerOptionResponse> answerOptionResponses =new ArrayList<>();
    private double point;

    public void setQuestionType(lms.enums.QuestionType questionType) {
    }
}
