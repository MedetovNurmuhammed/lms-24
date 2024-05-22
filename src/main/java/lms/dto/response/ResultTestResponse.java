package lms.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
@Getter
@Setter
@NoArgsConstructor
public class ResultTestResponse {
    private Long testId;
    private String testTitle;
    private List<AnswerQuestionResponse> answerQuestionResponses = new ArrayList<>();
    private double totalPoint;
}
