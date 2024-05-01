package lms.service.impl;

import lms.dto.response.SimpleResponse;
import lms.dto.response.TaskRequest;
import lms.repository.LessonRepository;
import lms.repository.TaskRepository;
import lms.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {
    private final TaskRepository taskRepository;
    private final LessonRepository lessonRepository;

    @Override
    public SimpleResponse createTask(Long lessonId, TaskRequest taskRequest) {
        lessonRepository.findById(lessonId).orElseThrow(()-> new IllegalArgumentException("Lesson does not exist"));

        return null;
    }
}
