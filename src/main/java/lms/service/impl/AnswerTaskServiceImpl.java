package lms.service.impl;

import com.amazonaws.services.wellarchitected.model.Answer;
import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import lms.dto.response.CommentResponse;
import lms.dto.response.FilterAnswerOfTaskResponse;
import lms.dto.request.AnswerTaskRequest;
import lms.dto.response.AnswerTaskResponse;
import lms.dto.response.SimpleResponse;
import lms.entities.*;
import lms.enums.TaskAnswerStatus;
import lms.repository.*;
import lms.service.AnswerTaskService;
import lms.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Transactional
public class AnswerTaskServiceImpl implements AnswerTaskService {
    private final UserRepository userRepository;
    private final StudentRepository studentRepository;
    private final CommentRepository commentRepository;
    private final NotificationRepository notificationRepository;
    private final NotificationService notificationService;
    private final InstructorRepository instructorRepository;
    private final AnswerTaskRepository answerTaskRepository;
    private final TaskRepository taskRepository;

    @Override
    public SimpleResponse save(Long taskId, AnswerTaskRequest answerTaskRequest) throws MessagingException {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByEmail(email).orElseThrow(() -> new NoSuchElementException("Пользователь не найден"));
        Task task = taskRepository.findById(taskId).orElseThrow(() -> new NoSuchElementException("Задание не найдено"));
        Student student = studentRepository.findByUserId(currentUser.getId()).orElseThrow(() ->
                new NoSuchElementException("Студент не найден"));
        Comment comment = new Comment();
        if (answerTaskRequest.comment() != null) {
            comment.setContent(answerTaskRequest.comment());
            comment.setUser(student.getUser());
            commentRepository.save(comment);
        }
        AnswerTask answerTask = new AnswerTask();
        answerTask.setTask(task);
        answerTask.setStudent(student);
        answerTask.setImage(answerTaskRequest.image());
        answerTask.setFile(answerTaskRequest.file());
        answerTask.getComments().add(comment);
        answerTask.setText(answerTaskRequest.text());
        if (task.getDeadline().isAfter(LocalDateTime.now())) {
            answerTask.setTaskAnswerStatus(TaskAnswerStatus.WAITING);
        } else if (task.getDeadline().isBefore(LocalDateTime.now())) {
            answerTask.setTaskAnswerStatus(TaskAnswerStatus.LATE);
        }
        answerTaskRepository.save(answerTask);
        comment.setAnswerTask(answerTask);
        String message = student.getUser().getFullName() + " отправил(a) домашнее задание по " + task.getTitle();
        getInstructorsByCourse(task, answerTask, message);
        return SimpleResponse.builder()
                .message("Ответ на домашнее задание успешно сохранен")
                .httpStatus(HttpStatus.OK)
                .build();
    }

    @Override
    public List<FilterAnswerOfTaskResponse> filterAnswerTask(Long taskId, TaskAnswerStatus answerStatus) {
        taskRepository.findById(taskId).orElseThrow(() -> new NoSuchElementException("Задание не найдено"));
        return answerTaskRepository.filterAnswerTask(taskId, answerStatus);
    }

    @Override
    public SimpleResponse update(Long answerTaskId, AnswerTaskRequest answerTaskRequest) throws MessagingException {
        AnswerTask answer = answerTaskRepository.findById(answerTaskId).orElseThrow(() ->
                new NoSuchElementException("Ответ на задание не найден"));
        Student student = answer.getStudent();
        Task task = answer.getTask();
        answer.setImage(answerTaskRequest.image());
        answer.setFile(answerTaskRequest.file());
        List<Comment> comments = answer.getComments();
        comments.forEach(comment1 -> {
            comment1.setContent(answerTaskRequest.comment());
            commentRepository.save(comment1);
        });

        answer.setText(answerTaskRequest.text());
        if (task.getDeadline().isAfter(LocalDateTime.now())) {
            answer.setTaskAnswerStatus(TaskAnswerStatus.WAITING);
        } else if (task.getDeadline().isBefore(LocalDateTime.now())) {
            answer.setTaskAnswerStatus(TaskAnswerStatus.LATE);
        }

        String message = student.getUser().getFullName() + " изменил(a) домашнее задание по " + task.getTitle();
        getInstructorsByCourse(task, answer, message);
        return SimpleResponse.builder()
                .message("Ответ на задание успешно обновлен")
                .httpStatus(HttpStatus.OK)
                .build();
    }

    @Override
    public AnswerTaskResponse findAnswerByTaskId(Long taskId) {
        taskRepository.findById(taskId).orElseThrow(() -> new NoSuchElementException("Задание не найдено"));
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        AnswerTask answerTask = answerTaskRepository.findByTaskId(taskId, email);
        return getAnswerTaskResponse(answerTask);
    }

    @Override
    public AnswerTaskResponse getAnswerById(Long answerId) {
        AnswerTask answer = answerTaskRepository.findById(answerId).orElseThrow(() -> new NoSuchElementException("Ответ не найден"));
        return getAnswerTaskResponse(answer);
    }

    private void getInstructorsByCourse(Task task, AnswerTask answerTask, String message) throws MessagingException {
        List<Instructor> instructors = instructorRepository.findByAnswerTask(task.getId());

        for (Instructor instructor : instructors) {
            Notification notification = new Notification();
            notification.setAnswerTask(answerTask);
            notification.setDescription(message);
            notification.setTitle("Ответ на домашнее задание");

            notificationRepository.save(notification);
            instructor.getNotificationStates().put(notification, false);
            notificationService.emailMessage(message, instructor.getUser().getEmail());
        }
    }

    private AnswerTaskResponse getAnswerTaskResponse(AnswerTask answerTask) {
        List<CommentResponse> commentResponses = new ArrayList<>();
        answerTask.getComments().forEach(comment -> {
            commentResponses.add(CommentResponse.builder()
                    .author(comment.getUser().getFullName())
                    .role(comment.getUser().getRole())
                    .content(comment.getContent())
                    .build());
        });
        return AnswerTaskResponse.builder()
                .text(answerTask.getText())
                .image(answerTask.getImage())
                .file(answerTask.getFile())
                .taskAnswerStatus(answerTask.getTaskAnswerStatus())
                .point(answerTask.getPoint())
                .comment(commentResponses)
                .build();
    }
}
