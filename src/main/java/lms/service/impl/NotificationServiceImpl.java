package lms.service.impl;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.transaction.Transactional;
import lms.dto.response.*;
import lms.entities.*;
import lms.enums.Role;
import lms.repository.*;
import lms.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
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
        log.info("{} успешно отправлен ", toEmail);
    }

    @Override
    public List<NotificationResponse> findAll(Boolean isView) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new NoSuchElementException("Пользователь не найден"));

        Map<Notification, Boolean> notificationStates = getNotificationStates(currentUser);

        return notificationStates.entrySet().stream()
                .map(entry -> mapToNotificationResponse(entry.getKey(), entry.getValue()))
                .filter(notificationResponse -> isView == null || notificationResponse.isView().equals(isView))
                .collect(Collectors.toList());
    }

    @Override
    public SimpleResponse findByNotificationId(Long notificationId) {
        Notification notification = notificationRepository.findNotificationById(notificationId)
                .orElseThrow(() -> new NoSuchElementException("Уведомление не найдено"));

        User currentUser = getCurrentUser();
        updateNotificationState(currentUser, notification);
        return SimpleResponse.builder()
                .message("Уведомление простмотрено")
                .httpStatus(HttpStatus.OK)
                .build();
    }

    private void updateNotificationState(User currentUser, Notification notification) {
        if (currentUser.getRole().equals(Role.STUDENT)) {
            Student student = studentRepository.findByUserId(currentUser.getId())
                    .orElseThrow(() -> new NoSuchElementException("Студент не найден"));
             notificationRepository.findNotificationInExtraTable(student.getId(),notification.getId())
                    .orElseThrow(()->new NoSuchElementException("Уведомление не найдено"));
            student.getNotificationStates().put(notification, true);
        } else if (currentUser.getRole().equals(Role.INSTRUCTOR)) {
            Instructor instructor = instructorRepository.findByUserId(currentUser.getId())
                    .orElseThrow(() -> new NoSuchElementException("Инструктор не найден"));
            notificationRepository.findNotificationInstructorInExtraTable( instructor.getId() ,notification.getId())
                    .orElseThrow(()->new NoSuchElementException("Уведомление не найдено"));
            instructor.getNotificationStates().put(notification, true);
        }
    }

    private Map<Notification, Boolean> getNotificationStates(User currentUser) {
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

    private NotificationResponse mapToNotificationResponse(Notification notification, Boolean isView) {
        return NotificationResponse.builder()
                .notificationId(notification.getId())
                .notificationTitle(notification.getTitle())
                .notificationDescription(notification.getDescription())
                .notificationSendDate(notification.getCreatedAt())
                .notificationTaskId(notification.getTask().getId())
                .isView(isView)
                .build();
    }

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.getByEmail(email);
    }

    @Override
    public SimpleResponse delete(Long notificationId) {
        Notification notification = notificationRepository.findNotificationById(notificationId)
                .orElseThrow(() -> new NoSuchElementException("Уведомление не найдено"));

        User currentUser = getCurrentUser();
        if (currentUser.getRole().equals(Role.STUDENT)) {
            Student student = studentRepository.findByUserId(currentUser.getId())
                    .orElseThrow(() -> new NoSuchElementException("Студент не найден"));
            notificationRepository.deleteNotificationFromExtraTableStudent(notification.getId(), student.getId());
        } else if (currentUser.getRole().equals(Role.INSTRUCTOR)) {
            Instructor instructor = instructorRepository.findByUserId(currentUser.getId())
                    .orElseThrow(() -> new NoSuchElementException("Инструктор не найден"));
            notificationRepository.deleteNotificationFromExtraTableInstructor(notification.getId(), instructor.getId());
        }
        return SimpleResponse.builder()
                .httpStatus(HttpStatus.OK)
                .message("Уведомление успешно удалено")
                .build();
    }
}
