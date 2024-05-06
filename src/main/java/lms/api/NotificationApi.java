package lms.api;

import io.swagger.v3.oas.annotations.Operation;
import lms.dto.response.FindNotificationTaskResponse;
import lms.dto.response.NotificationResponse;
import lms.dto.response.SimpleResponse;
import lms.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationApi {
    private final NotificationService notificationService;

    @Secured({"INSTRUCTOR", "STUDENT"})
    @Operation(summary = "Получить все уведомления", description = "Возвращает список всех уведомлений для текущего пользователя.")
    @GetMapping("/findAll")
    public List<NotificationResponse> getAllNotifications(@RequestParam Boolean isView) {
        return notificationService.findAll(isView);
    }

    @Secured({"INSTRUCTOR", "STUDENT"})
    @Operation(summary = "Найти уведомление по идентификатору", description = "Находит уведомление по указанному идентификатору.")
    @GetMapping("/findById/{notificationId}")
    public FindNotificationTaskResponse findNotification(@PathVariable Long notificationId) {
        return notificationService.findById(notificationId);
    }

    @Secured({"INSTRUCTOR", "STUDENT"})
    @Operation(summary = "Удалить уведомление по идентификатору", description = "Находит уведомление по указанному идентификатору.")
    @DeleteMapping("/delete/{notificationId}")
    public SimpleResponse deleteNotifications(@PathVariable Long notificationId) {
        return notificationService.delete(notificationId);
    }

}


