package lms.api;

import io.swagger.v3.oas.annotations.Operation;
import lms.dto.response.DataResponses;
import lms.service.AnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
public class AnalyticsApi {
    private final AnalyticsService analyticsService;
    @Secured("ADMIN")
    @GetMapping("getAllAnalytics")
    @Operation(summary = "Получить все данные ", description = "Количество обучающиеся,окончавшие" +
            " и курс,инструктор! Авторизация: Админ!")
    public List<DataResponses> getAllStudentsCount() {

        return analyticsService.getAllAnalyticsCount();
    }
}
