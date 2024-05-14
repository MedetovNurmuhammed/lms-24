package lms.service;

import lms.dto.response.AllQuestionResponse;
import lms.dto.response.QuestionResponse;

public interface QuestionService {
    AllQuestionResponse findAllQuestions(Long testId);
}
