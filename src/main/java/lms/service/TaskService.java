package lms.service;

import jakarta.mail.MessagingException;
import lms.dto.request.TaskRequest;
import lms.dto.response.AllTaskResponse;
import lms.dto.response.SimpleResponse;
import lms.dto.response.TaskResponse;

public interface TaskService {
    SimpleResponse createTask(Long lessonId, TaskRequest taskRequest) throws MessagingException;

    SimpleResponse updateTask( Long taskId, TaskRequest taskRequest) throws MessagingException;

    SimpleResponse deleteTask(Long taskId);

    TaskResponse findById(Long taskId);

    AllTaskResponse findAllTaskByLessonId(int page, int size,Long lessonId);
}
