package lms.service;

import lms.dto.response.QuestionResponse;

public interface QuestionService {
    QuestionResponse findAllQuestions(Long testId);
}
