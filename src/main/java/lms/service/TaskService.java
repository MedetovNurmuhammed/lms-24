package lms.service;

import jakarta.mail.MessagingException;
import lms.dto.request.TaskRequest;
import lms.dto.response.AllTaskResponse;
import lms.dto.response.SimpleResponse;
import lms.dto.response.TaskResponse;

public interface TaskService {
    SimpleResponse createTask(Long lessonId, TaskRequest taskRequest) throws MessagingException;

    SimpleResponse delete(Long taskId);

    SimpleResponse updateTask( Long taskId, TaskRequest taskRequest) throws MessagingException;

    TaskResponse findById(Long taskId);

    AllTaskResponse findAllTaskByLessonId(Long lessonId);
}
