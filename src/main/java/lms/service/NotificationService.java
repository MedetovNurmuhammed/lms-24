package lms.service;

import jakarta.mail.MessagingException;

import lms.dto.response.NotificationResponse;
import lms.dto.response.SimpleResponse;

import java.util.List;

public interface NotificationService {

    void emailMessage(String message, String toEmail) throws MessagingException;

    List<NotificationResponse> findAll(Boolean isView);

    SimpleResponse findByNotificationId(Long notificationId);

    SimpleResponse delete(Long notificationId);
}
