package lms.service;

import lms.dto.response.SimpleResponse;

public interface QuestionService {
    SimpleResponse delete(Long questionId);
}
