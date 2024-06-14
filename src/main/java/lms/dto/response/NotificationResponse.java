package lms.dto.response;

import lombok.Builder;

import java.time.LocalDate;

@Builder
public record NotificationResponse(
        Long notificationId,
        Long courseId,
        Long lessonId,
        Long taskId,
        String notificationTitle,
        String notificationDescription,
        LocalDate notificationSendDate,
        Long answerTaskId,
        Boolean isView) {
}
