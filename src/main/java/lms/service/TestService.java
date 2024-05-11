package lms.service;

import lms.dto.response.SimpleResponse;

public interface TestService {
    SimpleResponse createTest();

    SimpleResponse delete(Long testId);
}
