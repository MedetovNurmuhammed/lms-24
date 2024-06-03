package lms.dto.response;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class TaskRatingResponse {
    private Long id;
    private   String taskTitle;
    private AnswerTaskRatingResponse answerTaskRatingResponses;
}
