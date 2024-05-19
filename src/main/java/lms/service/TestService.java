package lms.service;

import lms.dto.request.TestRequest;
import lms.dto.request.UpdateTestRequest;
import lms.dto.response.AllTestResponse;
import lms.dto.response.SimpleResponse;
import lms.dto.response.TestResponse;
import lms.dto.response.TestResponseWithStudents;

public interface TestService {
    SimpleResponse  saveTest(Long lessonId, TestRequest testRequest);

    SimpleResponse update(Long testId, UpdateTestRequest updateTestRequest);

    SimpleResponse accessToTest(Long testId);

    SimpleResponse delete(Long testId);

    TestResponseWithStudents findById(Long testId);

    TestResponse findTestByIdForEdit(Long testId);

    AllTestResponse findAll(Long lessonId);
}
