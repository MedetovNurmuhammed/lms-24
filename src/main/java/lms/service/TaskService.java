package lms.service;

import lms.dto.response.SimpleResponse;

public interface TaskService {
    SimpleResponse delete(Long taskId);
}
