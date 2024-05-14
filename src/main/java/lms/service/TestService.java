package lms.service;

import lms.dto.request.TestRequest;
import lms.dto.request.UpdateTestRequest;
import lms.dto.response.SimpleResponse;
import lms.dto.response.TestResponse;

public interface TestService {
    SimpleResponse  saveTest(Long lessonId, TestRequest testRequest);

    SimpleResponse update(Long testId, UpdateTestRequest updateTestRequest);

    SimpleResponse enableToStart(Long testId);

    SimpleResponse delete(Long testId);

    TestResponse findById(Long testId);
}
