package lms.service;

import jakarta.mail.MessagingException;
import lms.dto.response.FindNotificationTaskResponse;
import lms.dto.response.NotificationResponse;
import lms.dto.response.SimpleResponse;
import java.util.List;

public interface NotificationService {

    void emailMessage(String message, String toEmail) throws MessagingException;

    List<NotificationResponse> findAll(Boolean isView);

    FindNotificationTaskResponse findById(Long notificationId);

    SimpleResponse delete(Long notificationId);
}
