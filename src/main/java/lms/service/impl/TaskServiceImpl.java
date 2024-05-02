package lms.service.impl;

import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import lms.dto.response.SimpleResponse;
import lms.dto.request.TaskRequest;
import lms.dto.response.TaskResponse;
import lms.entities.*;
import lms.enums.Type;
import lms.repository.*;
import lms.service.NotificationService;
import lms.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class TaskServiceImpl implements TaskService {
    private final TaskRepository taskRepository;
    private final LessonRepository lessonRepository;
    private final UserRepository userRepository;
    private final InstructorRepository instructorRepository;
    private final NotificationRepository notificationRepository;
    private final NotificationService notificationService;
    private final StudentRepository studentRepository;
    private final TrashRepository trashRepository;

    @Override
    public SimpleResponse createTask(Long lessonId, TaskRequest taskRequest) throws MessagingException {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.getByEmail(email);
        Instructor instructor = instructorRepository.findByUserId(currentUser.getId());

        Lesson lesson = lessonRepository.findById(lessonId).orElseThrow(() -> new IllegalArgumentException("Lesson does not exist"));
        Task task = new Task();
        task.setLesson(lesson);
        task.setTitle(taskRequest.title());
        task.setCode(taskRequest.code());
        task.setDescription(taskRequest.description());
        task.setFile(taskRequest.file());
        task.setImage(taskRequest.image());
        task.setInstructor(instructor);
        task.setDeadline(taskRequest.deadline());
        task.setLinks(taskRequest.links());

        Task savedTask = taskRepository.save(task);
        return SimpleResponse.builder()
                .httpStatus(HttpStatus.OK)
                .message("Успешно создана")
                .build();
    }

    @Override
    public SimpleResponse updateTask( Long taskId, TaskRequest taskRequest) {
        Task task = getById(taskId);
        task.setTitle(taskRequest.title());
        task.setCode(taskRequest.code());
        task.setDescription(taskRequest.description());
        task.setFile(taskRequest.file());
        task.setImage(taskRequest.image());
        task.setDeadline(LocalDateTime.from(taskRequest.deadline()));
        task.setLinks(taskRequest.links());

        return SimpleResponse.builder()
                .httpStatus(HttpStatus.OK)
                .message("Успешно обновлено")
                .build();
    }

    @Override
    public SimpleResponse deleteTask(Long taskId) {
        Task task = getById(taskId);
        Trash trash = new Trash();
        trash.setTask(task);
        trash.setType(Type.TASK);
        task.setTrash(trash);
        trashRepository.save(trash);
        return SimpleResponse.builder()
                .httpStatus(HttpStatus.OK)
                .message("Успешно удалено")
                .build();
    }

    @Override
    public TaskResponse findById(Long taskId) {
        Task task = getById(taskId);
        return TaskResponse.builder()
                .id(task.getId())
                .title(task.getTitle())
                .code(task.getCode())
                .description(task.getDescription())
                .deadline(task.getDeadline())
                .links(task.getLinks())
                .image(task.getImage())
                .file(task.getFile())
                .build();
    }

    Task getById(Long taskId) {
        return taskRepository.findById(taskId).orElseThrow(() -> new IllegalArgumentException("Task does not exist"));
    }
}
