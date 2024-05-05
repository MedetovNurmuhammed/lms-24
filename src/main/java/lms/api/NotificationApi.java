package lms.api;

import io.swagger.v3.oas.annotations.Operation;
import lms.dto.response.NotificationResponse;
import lms.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationApi {
    private final NotificationService notificationService;

    @Secured({"INSTRUCTOR","STUDENT"})
    @Operation(summary = "Получить все уведомления", description = "Возвращает список всех уведомлений для текущего пользователя.")
    @GetMapping("/findAll")
    public List<NotificationResponse> getAllNotifications() {
        return notificationService.findAll();
    }

    @Secured({"INSTRUCTOR","STUDENT"})
    @Operation(summary = "Найти уведомление по идентификатору", description = "Находит уведомление по указанному идентификатору.")
    @GetMapping("/findById/{notificationId}")
    public List<NotificationResponse> findNotifications(@PathVariable Long notificationId) {
        return notificationService.findById(notificationId);
    }

}


