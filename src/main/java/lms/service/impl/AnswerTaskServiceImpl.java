package lms.service.impl;

import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import lms.dto.request.CheckAnswerRequest;
import lms.dto.response.CommentResponse;
import lms.dto.response.FilterAnswerOfTaskResponse;
import lms.dto.request.AnswerTaskRequest;
import lms.dto.response.AnswerTaskResponse;
import lms.dto.response.SimpleResponse;
import lms.entities.*;
import lms.enums.TaskAnswerStatus;
import lms.exceptions.AlreadyExistsException;
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
        User currentUser = getCurrentUser();
        Task task = taskRepository.findTaskById(taskId)
                .orElseThrow(() -> new NoSuchElementException("Задание не найдено"));
        Student student = studentRepository.findByUserId(currentUser.getId())
                .orElseThrow(() -> new NoSuchElementException("Студент не найден"));

        Boolean b = answerTaskRepository.existsByTaskId(taskId, student.getId());
        if (b) throw new AlreadyExistsException("Ваш ответ уже существует");

        Comment comment = new Comment();
        if (answerTaskRequest.comment() != null) {
            comment = saveComment(answerTaskRequest.comment(), currentUser);
        }
        AnswerTask answerTask = new AnswerTask();
        buildAnswerTask(answerTask, task, student, answerTaskRequest, comment);

        setTaskAnswerStatus(answerTask, task);

        answerTaskRepository.save(answerTask);


        notifyInstructors(task, answerTask, student);

        return SimpleResponse.builder()
                .message("Ответ на домашнее задание успешно сохранен")
                .httpStatus(HttpStatus.OK)
                .build();
    }

    @Override
    public AnswerTaskResponse findAnswerByTaskId(Long taskId) {
        taskRepository.findTaskById(taskId).orElseThrow(() -> new NoSuchElementException("Задание не найдено"));
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        AnswerTask answerTask = answerTaskRepository.findByTaskId(taskId, email).orElseThrow(() -> new NoSuchElementException("Ответ на задание не найден для данного пользователя"));
        return getAnswerTaskResponse(answerTask);
    }

    @Override
    public AnswerTaskResponse getAnswerById(Long answerId) {
        AnswerTask answer = answerTaskRepository.findAnswerTaskById(answerId).orElseThrow(() ->
                new NoSuchElementException("Ответ не найден"));
        return getAnswerTaskResponse(answer);
    }

    @Override
    public List<FilterAnswerOfTaskResponse> filterAnswerTask(Long taskId, TaskAnswerStatus answerStatus) {
        taskRepository.findTaskById(taskId).orElseThrow(() -> new NoSuchElementException("Задание не найдено"));
        return answerTaskRepository.filterAnswerTask(taskId, answerStatus);
    }

    @Override
    public SimpleResponse update(Long answerTaskId, AnswerTaskRequest answerTaskRequest) throws MessagingException {
        AnswerTask answer = findAnswerTaskById(answerTaskId);
        if (answer.getTaskAnswerStatus().equals(TaskAnswerStatus.ACCEPTED))
            throw new AlreadyExistsException("Ваш ответ на задание уже проверено");
        Task task = answer.getTask();

        updateAnswerTask(answer, answerTaskRequest, task);

        String message = generateUpdateMessage(answer);
        notifyInstructors(task, answer, message);

        return SimpleResponse.builder()
                .message("Ответ на задание успешно обновлен")
                .httpStatus(HttpStatus.OK)
                .build();
    }

    @Override
    public SimpleResponse checkAnswer(Long answerId, CheckAnswerRequest checkAnswerRequest) throws MessagingException {
        User currentUser = getCurrentUser();
        AnswerTask answer = findAnswerTaskById(answerId);
        if (checkAnswerRequest.isAccept()) {
            answer.setTaskAnswerStatus(TaskAnswerStatus.ACCEPTED);
        } else {
            answer.setTaskAnswerStatus(TaskAnswerStatus.REJECTED);
        }
        answer.setPoint(checkAnswerRequest.point());
        if (checkAnswerRequest.comment() != null) {
            Comment comment = saveComment(checkAnswerRequest.comment(), currentUser);
            answer.getComments().add(comment);
            comment.setAnswerTask(answer);
        }
        Student student = answer.getStudent();
        Instructor instructor = instructorRepository.findByUserId(currentUser.getId()).orElseThrow(() ->
                new NoSuchElementException("Инструктор не найден"));
        String message = instructor.getUser().getFullName() + " оценил(a) вашу работу по " + answer.getTask().getTitle();
        Notification notification = createNotification(answer, message);
//        student.getNotificationStates().put(notification, false);
        notificationService.emailMessage(message, student.getUser().getEmail());
        return SimpleResponse.builder()
                .httpStatus(HttpStatus.OK)
                .message("Успешно проверено ответ")
                .build();
    }

    @Override
    public List<String> getNotAnswered(Long taskId) {
        taskRepository.findById(taskId).orElseThrow(() -> new NoSuchElementException("Задание не найдено"));
        List<Long> studentIds = studentRepository.findStudentIdByCourseId(taskId);
        return studentRepository.findUserNamesByTask(studentIds, taskId);
    }


    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new NoSuchElementException("Пользователь не найден"));
    }



    private Comment saveComment(String comment, User author) {
        Comment newComment = new Comment();
        newComment.setContent(comment);
        newComment.setUser(author);
        return commentRepository.save(newComment);
    }

    private void buildAnswerTask(AnswerTask answerTask, Task task, Student student, AnswerTaskRequest answerTaskRequest, Comment comment) {
        answerTask.setTask(task);
        answerTask.setStudent(student);
        answerTask.setImage(answerTaskRequest.image());
        answerTask.setFile(answerTaskRequest.file());
        answerTask.setText(answerTaskRequest.text());
        setTaskAnswerStatus(answerTask,task);
        if (comment != null) {
            answerTask.getComments().add(comment);
            comment.setAnswerTask(answerTask);
        }
    }

    private void setTaskAnswerStatus(AnswerTask answerTask, Task task) {
        if (task.getDeadline().isAfter(LocalDateTime.now())) {
            answerTask.setTaskAnswerStatus(TaskAnswerStatus.WAITING);
        } else if (task.getDeadline().isBefore(LocalDateTime.now())) {
            answerTask.setTaskAnswerStatus(TaskAnswerStatus.LATE);
        }
    }

    private void notifyInstructors(Task task, AnswerTask answer, Student student) throws MessagingException {
        String message = student.getUser().getFullName() + " отправил(a) домашнее задание по " + task.getTitle();
        notifyInstructors(task, answer,message);
    }

    private AnswerTask findAnswerTaskById(Long answerTaskId) {
        return answerTaskRepository.findById(answerTaskId)
                .orElseThrow(() -> new NoSuchElementException("Ответ на задание не найден"));
    }

    private void updateAnswerTask(AnswerTask answer, AnswerTaskRequest answerTaskRequest, Task task) {
        answer.setImage(answerTaskRequest.image());
        answer.setFile(answerTaskRequest.file());
        updateComments(answer, answerTaskRequest);
        answer.setText(answerTaskRequest.text());
        setTaskAnswerStatus(answer, task);
    }

    private void updateComments(AnswerTask answer, AnswerTaskRequest answerTaskRequest) {
        List<Comment> comments = answer.getComments();
        comments.forEach(comment -> {
            comment.setContent(answerTaskRequest.comment());
            commentRepository.save(comment);
        });
    }

    private String generateUpdateMessage(AnswerTask answer) {
        Student student = answer.getStudent();
        Task task = answer.getTask();
        return student.getUser().getFullName() + " изменил(a) домашнее задание по " + task.getTitle();
    }


    private void notifyInstructors(Task task, AnswerTask answerTask, String message) throws MessagingException {
        List<Instructor> instructors = instructorRepository.findByAnswerTask(task.getId());
        Notification notification = createNotification(answerTask, message);
        for (Instructor instructor : instructors) {
            sendNotification(notification, instructor, message);
        }
    }

    private void sendNotification(Notification notification, Instructor instructor, String message) throws MessagingException {
//        instructor.getNotificationStates().put(notification, false);
        notificationService.emailMessage(message, instructor.getUser().getEmail());
    }

    private Notification createNotification(AnswerTask answerTask, String message) {
        Notification notification = new Notification();
        notification.setAnswerTask(answerTask);
        notification.setDescription(message);
        notification.setTitle("Ответ на домашнее задание");
        return notificationRepository.save(notification);

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
