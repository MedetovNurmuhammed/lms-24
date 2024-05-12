package lms.service;

import lms.dto.request.TestRequest;
import lms.dto.response.SimpleResponse;

public interface TestService {
    SimpleResponse  saveTest(Long lessonId, TestRequest testRequest);
}
