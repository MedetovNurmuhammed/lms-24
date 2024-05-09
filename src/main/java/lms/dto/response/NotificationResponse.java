package lms.dto.response;

import lombok.Builder;

import java.time.LocalDate;

@Builder
public record NotificationResponse(
        Long notificationId,
        String notificationTitle,
        String notificationDescription,
        LocalDate notificationSendDate,
        Long notificationTaskId,
        Boolean isView) {
}
