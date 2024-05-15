package lms.service;

import lms.dto.request.AnswerTestRequest;
import lms.dto.response.ResultTestResponse;
import lms.dto.response.SimpleResponse;

public interface ResultTestService {
    SimpleResponse saveResult(Long testId, AnswerTestRequest answerRequest);

    ResultTestResponse findResultOfCurrentStudent(Long testId);

    ResultTestResponse findResultTestById(Long resultTestId);
}
