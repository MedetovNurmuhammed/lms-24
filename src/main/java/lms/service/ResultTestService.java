package lms.service;

import lms.dto.request.AnswerTestRequest;
import lms.dto.response.ResultTestResponse;

public interface ResultTestService {
    ResultTestResponse result(Long testId, AnswerTestRequest answerRequest);

    ResultTestResponse findResultOfCurrentStudent();

    ResultTestResponse findResultTestById(Long resultTestId);
}
