package lms.dto.response;

import java.time.LocalDate;

public record NotificationResponse(
        Long notificationId,
        String notificationTitle,
        String notificationDescription,
        LocalDate notificationSendDate,
        Long notificationTaskId ) {
}
