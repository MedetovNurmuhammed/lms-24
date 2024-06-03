package lms.service.impl;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.transaction.Transactional;
import lms.dto.response.FindNotificationTaskResponse;
import lms.dto.response.NotificationResponse;
import lms.dto.response.SimpleResponse;
import lms.entities.*;
import lms.enums.Role;
import lms.repository.InstructorRepository;
import lms.repository.NotificationRepository;
import lms.repository.StudentRepository;
import lms.repository.UserRepository;
import lms.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final JavaMailSender javaMailSender;
    private final UserRepository userRepository;
    private final InstructorRepository instructorRepository;
    private final StudentRepository studentRepository;

    @Override
    public void emailMessage(String message, String toEmail) throws MessagingException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);
        mimeMessageHelper.setFrom("PEAKSOFT");
        mimeMessageHelper.setTo(toEmail); // Используем переданный адрес электронной почты
        mimeMessageHelper.setText(message);
        mimeMessageHelper.setSubject("КУРСЫ ПРОГРАММИРОВАНИЕ PEAKSOFT!");
        javaMailSender.send(mimeMessage);
        System.out.println("Email успешно отправлен");
    }

    @Override
    public List<NotificationResponse> findAll(Boolean isView) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new NoSuchElementException("Пользователь не найден"));

        Map<Long, Boolean> notificationStates = getNotificationStates(currentUser);

        return notificationStates.entrySet().stream()
                .map(entry -> mapToNotificationResponse(entry.getKey(), entry.getValue()))
                .filter(notificationResponse -> isView == null || notificationResponse.isView().equals(isView))
                .collect(Collectors.toList());
    }

    private Map<Long, Boolean> getNotificationStates(User currentUser) {
        if (currentUser.getRole().equals(Role.STUDENT)) {
            Student student = studentRepository.findByUserId(currentUser.getId())
                    .orElseThrow(() -> new NoSuchElementException("Студент не найден"));
            return student.getNotificationStates();
        } else if (currentUser.getRole().equals(Role.INSTRUCTOR)) {
            Instructor instructor = instructorRepository.findByUserId(currentUser.getId())
                    .orElseThrow(() -> new NoSuchElementException("Инструктор не найден"));
            return instructor.getNotificationStates();
        }
        return Collections.emptyMap();
    }

    private NotificationResponse mapToNotificationResponse(Long id, Boolean isView) {
        Notification notification = notificationRepository.getReferenceById(id);
        return NotificationResponse.builder()
                .notificationId(notification.getId())
                .notificationTitle(notification.getTitle())
                .notificationDescription(notification.getDescription())
                .notificationSendDate(notification.getCreatedAt())
                .notificationTaskId(notification.getTask().getId())
                .isView(isView)
                .build();
    }

    @Override
    public FindNotificationTaskResponse findById(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new NoSuchElementException("Уведомление не найдено"));

        User currentUser = getCurrentUser();
        updateNotificationState(currentUser, notification);

        Task task = notification.getTask();
        return mapToFindNotificationTaskResponse(task);
    }

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.getByEmail(email);
    }

    private void updateNotificationState(User currentUser, Notification notification) {
        if (currentUser.getRole().equals(Role.STUDENT)) {
            Student student = studentRepository.findByUserId(currentUser.getId())
                    .orElseThrow(() -> new NoSuchElementException("Студент не найден"));
            student.getNotificationStates().put(notification.getId(), true);
        } else if (currentUser.getRole().equals(Role.INSTRUCTOR)) {
            Instructor instructor = instructorRepository.findByUserId(currentUser.getId())
                    .orElseThrow(() -> new NoSuchElementException("Инструктор не найден"));
            instructor.getNotificationStates().put(notification.getId(), true);
        }
    }

    private FindNotificationTaskResponse mapToFindNotificationTaskResponse(Task task) {
        return FindNotificationTaskResponse.builder()
                .id(task.getId())
                .image(task.getImage())
                .title(task.getTitle())
                .code(task.getCode())
                .file(task.getFile())
                .deadline(task.getDeadline())
                .links(task.getLinks())
                .build();
    }

    @Override
    public SimpleResponse delete(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new NoSuchElementException("Уведомление не найдено"));

        User currentUser = getCurrentUser();
        if (currentUser.getRole().equals(Role.STUDENT)) {
            studentRepository.findByUserId(currentUser.getId())
                    .orElseThrow(() -> new NoSuchElementException("Студент не найден"));
            notificationRepository.deleteNotificationFromExtraTableStudent(notification.getId());
        } else if (currentUser.getRole().equals(Role.INSTRUCTOR)) {
            instructorRepository.findByUserId(currentUser.getId())
                    .orElseThrow(() -> new NoSuchElementException("Инструктор не найден"));
            notificationRepository.deleteNotificationFromExtraTableInstructor(notification.getId());
        }
        return SimpleResponse.builder()
                .httpStatus(HttpStatus.OK)
                .message("Уведомление успешно удалено")
                .build();
    }
}
