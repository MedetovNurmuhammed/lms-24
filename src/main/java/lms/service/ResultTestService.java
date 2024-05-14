package lms.service;

import lms.dto.request.AnswerTestRequest;
import lms.dto.response.ResultTestResponse;

import java.security.Principal;

public interface ResultTestService {
    ResultTestResponse result(Long testId, AnswerTestRequest answerRequest);

    ResultTestResponse findResultOfCurrentStudent();
}
