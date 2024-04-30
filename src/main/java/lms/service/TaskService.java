package lms.service;

import lms.dto.response.SimpleResponse;
import lms.dto.response.TaskRequest;

public interface TaskService {
    SimpleResponse createTask(Long lessonId, TaskRequest taskRequest);

    SimpleResponse delete(Long taskId);
}
