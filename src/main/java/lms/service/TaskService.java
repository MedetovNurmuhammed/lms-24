package lms.service;

import jakarta.mail.MessagingException;
import lms.dto.request.TaskRequest;
import lms.dto.response.SimpleResponse;
import lms.dto.response.TaskResponse;

public interface TaskService {
    SimpleResponse createTask(Long lessonId, TaskRequest taskRequest) throws MessagingException;

    SimpleResponse updateTask( Long taskId, TaskRequest taskRequest);

    SimpleResponse deleteTask(Long taskId);

    TaskResponse findById(Long taskId);
}
