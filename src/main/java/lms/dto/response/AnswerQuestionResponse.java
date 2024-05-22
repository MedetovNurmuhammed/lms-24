package lms.dto.response;

import lms.enums.QuestionType;
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
    QuestionType questionType;
    private List<AnswerOptionResponse> answerOptionResponses =new ArrayList<>();
    private double point;
    public void setQuestionType(lms.enums.QuestionType questionType) {
        this.questionType = questionType;
    }
}
