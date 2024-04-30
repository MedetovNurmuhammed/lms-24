package lms.service;

import lms.dto.response.SimpleResponse;
import lms.dto.request.TaskRequest;

public interface TaskService {
    SimpleResponse createTask(Long lessonId, TaskRequest taskRequest);
}
