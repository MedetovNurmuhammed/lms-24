package lms.service;

import jakarta.mail.MessagingException;
import lms.dto.response.NotificationResponse;

import java.util.List;

public interface NotificationService {

    void emailMessage(String message, String toEmail) throws MessagingException;

    List<NotificationResponse> findAll();

    List<NotificationResponse> findById(Long notificationId);
}
