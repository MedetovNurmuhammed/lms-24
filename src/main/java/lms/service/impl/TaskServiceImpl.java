package lms.service.impl;

import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import lms.dto.response.AllTaskResponse;
import lms.dto.response.SimpleResponse;
import lms.dto.request.TaskRequest;
import lms.dto.response.TaskResponse;
import lms.entities.*;
import lms.enums.Type;
import lms.exceptions.BadRequestException;
import lms.exceptions.NotFoundException;
import lms.repository.InstructorRepository;
import lms.repository.LessonRepository;
import lms.repository.TaskRepository;
import lms.repository.NotificationRepository;
import lms.repository.StudentRepository;
import lms.repository.TrashRepository;
import lms.repository.UserRepository;
import lms.service.NotificationService;
import lms.service.TaskService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class TaskServiceImpl implements TaskService {
    private final TaskRepository taskRepository;
    private final LessonRepository lessonRepository;
    private final UserRepository userRepository;
    private final InstructorRepository instructorRepository;
    private final NotificationRepository notificationRepository;
    private final StudentRepository studentRepository;
    private final TrashRepository trashRepository;
    private final NotificationService notificationService;


    @Override
    public SimpleResponse createTask(Long lessonId, TaskRequest taskRequest) throws MessagingException {
        Instructor instructor = getCurrentInstructor();

        Lesson lesson = lessonRepository.findLessonById(lessonId).orElseThrow(() -> new IllegalArgumentException("Урок не существует"));
        if (lesson.getTrash() == null) {
            Task task = new Task();
            task.setLesson(lesson);
            task.setTitle(taskRequest.title());
            task.setCode(taskRequest.code());
            task.setDescription(taskRequest.description());
            task.setFile(taskRequest.file());
            task.setImage(taskRequest.image());
            task.setDeadline(taskRequest.deadline());
            task.setLinks(taskRequest.links());

            Task savedTask = taskRepository.save(task);
            String message = instructor.getUser().getFullName() + " добавил(a) новую задачу для урока " + lesson.getTitle();
            getStudentsByCourse(lesson, savedTask, message);
            return SimpleResponse.builder()
                    .httpStatus(HttpStatus.OK)
                    .message("Успешно создана")
                    .build();
        } else throw new BadRequestException("Урок может быть в корзине!");
    }

    private void getStudentsByCourse(Lesson lesson, Task savedTask, String message) throws MessagingException {
        List<Student> students = studentRepository.findByCourseId(lesson.getCourse().getId());

        for (Student student : students) {
            Notification notification = new Notification();
            notification.setTask(savedTask);
            notification.setDescription(message);
            notification.setTitle("Новое домашнее задание");

            notificationRepository.save(notification);
            student.getNotificationStates().put(notification, false);
            notificationService.emailMessage(message, student.getUser().getEmail());
        }
    }

    @Override
    public SimpleResponse updateTask(Long taskId, TaskRequest taskRequest) throws MessagingException {
        Task task = getById(taskId);
        if (task.getTrash() == null) {
            task.setTitle(taskRequest.title());
            task.setCode(taskRequest.code());
            task.setDescription(taskRequest.description());
            task.setFile(taskRequest.file());
            task.setImage(taskRequest.image());
            task.setDeadline(LocalDateTime.from(taskRequest.deadline()));
            task.setLinks(taskRequest.links());

            Instructor instructor = getCurrentInstructor();
            Task savedTask = taskRepository.save(task);
            Lesson lesson = savedTask.getLesson();
            String message = instructor.getUser().getFullName() + " именил(a) задачу для урока " + lesson.getTitle();
            getStudentsByCourse(lesson, savedTask, message);
            return SimpleResponse.builder()
                    .httpStatus(HttpStatus.OK)
                    .message("Успешно обновлено")
                    .build();
        } else throw new BadRequestException("Задача может быть в корзине!");
    }

    @Override
    public SimpleResponse deleteTask(Long taskId) {
        Task task = getById(taskId);
        if (task.getTrash() == null) {
            Trash trash = new Trash();
            trash.setTask(task);
            trash.setType(Type.TASK);
            task.setTrash(trash);
            trashRepository.save(trash);
            return SimpleResponse.builder()
                    .httpStatus(HttpStatus.OK)
                    .message("Успешно удалено")
                    .build();
        } else throw new BadRequestException("Задача может быть в корзине!");
    }

    @Override
    public TaskResponse findById(Long taskId) {
        Task task = getById(taskId);
        if (task.getTrash() == null) {
            log.error(String.valueOf(task.getLinks()));
            return convertToTaskResponse(task);
        }else throw new lms.exceptions.BadRequestException("Задача может быть в корзине!");
    }

    @Override
    public AllTaskResponse findAllTaskByLessonId(Long lessonId) {
        lessonRepository.findLessonById(lessonId).orElseThrow(() -> new IllegalArgumentException("Урок не существует"));
        List<Task> responsePage = taskRepository.findAll(lessonId);
        List<TaskResponse> taskResponses = new ArrayList<>();
        responsePage.forEach(task -> {
            TaskResponse taskResponse = convertToTaskResponse(task);
            taskResponses.add(taskResponse);
        });
        return AllTaskResponse.builder()
                .taskResponse(taskResponses)
                .build();
    }


    Task getById(Long taskId) {
        return taskRepository.findTaskById(taskId).orElseThrow(() -> new IllegalArgumentException("Задание не существует"));
    }

    Instructor getCurrentInstructor() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.getByEmail(email);
        return instructorRepository.findByUserId(currentUser.getId()).orElseThrow(() ->
                new NoSuchElementException("Инструктор с id:" + currentUser.getId() + " не найден"));
    }

    private TaskResponse convertToTaskResponse(Task task) {
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

    @Override
    @Transactional
    public SimpleResponse delete(Long taskId) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.getByEmail(email);
        Instructor instructor = instructorRepository.findByUserId(currentUser.getId()).
                orElseThrow(() -> new NotFoundException("Инструктор не найден!"));
        taskRepository.findAll();
        Task task = taskRepository.findById(taskId).
                orElseThrow(() -> new NotFoundException("Задача не найдена!"));
        Trash trash = new Trash();
        trash.setName(task.getTitle());
        trash.setType(Type.TASK);
        trash.setDateOfDelete(ZonedDateTime.now());
        trash.setTask(task);
        trash.setInstructor(instructor);
        instructor.getTrashes().add(trash);
        task.setTrash(trash);
        trashRepository.save(trash);
        return SimpleResponse.builder()
                .httpStatus(HttpStatus.OK)
                .message("Успешно добавлено в корзину")
                .build();
    }

    public void deleteTaskById(Long taskId) {
        Task task = taskRepository.findById(taskId).
                orElseThrow(() -> new NotFoundException("Not found"));

        taskRepository.deleteById(task.getId());
        taskRepository.deleteById(taskId);
    }
}
