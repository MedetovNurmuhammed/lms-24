package lms.api;

import io.swagger.v3.oas.annotations.Operation;
import lms.dto.response.AnswerTaskResponse;
import lms.dto.response.FindNotificationTaskResponse;
import lms.dto.response.NotificationResponse;
import lms.dto.response.SimpleResponse;
import lms.service.AnswerTaskService;
import lms.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@CrossOrigin(origins = "*",maxAge = 3600)
public class NotificationApi {
    private final NotificationService notificationService;
    private final AnswerTaskService answerTaskService;

    @Secured({"INSTRUCTOR", "STUDENT"})
    @Operation(summary = "Получить все уведомления", description = "Возвращает список всех уведомлений для текущего пользователя.Авторизация студент и инструктор")
    @GetMapping("/findAll")
    public List<NotificationResponse> getAllNotifications(@RequestParam Boolean isView) {
        return notificationService.findAll(isView);
    }

    @Secured({"INSTRUCTOR", "STUDENT"})
    @Operation(summary = "Найти уведомление по идентификатору", description = "Находит уведомление по указанному идентификатору.Авторизация студент и инструктор")
    @GetMapping("/{notificationId}")
    public SimpleResponse findNotification(@PathVariable Long notificationId) {
        return notificationService.findByNotificationId(notificationId);
    }

    @Secured({"INSTRUCTOR", "STUDENT"})
    @Operation(summary = "Удалить уведомление по идентификатору", description = "Находит уведомление по указанному идентификатору.Авторизация студент и инструктор")
    @DeleteMapping("/{notificationId}")
    public SimpleResponse deleteNotifications(@PathVariable Long notificationId) {
        return notificationService.delete(notificationId);
    }

}


