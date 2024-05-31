package lms.api;

import io.swagger.v3.oas.annotations.Operation;
import lms.dto.response.CourseAnalyticsResponse;
import lms.dto.response.StudentsAnalyticsResponse;
import lms.service.AnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
@CrossOrigin(origins = "*",maxAge = 3600)
public class AnalyticsApi {
    private final AnalyticsService analyticsService;
    @GetMapping("getAllStudentsCount")
    @Operation(summary = "Получить все данные о студентах",description = "Количество обучающиеся,окончавшие" +
            " и всего! Авторизация: Админ!")


    public StudentsAnalyticsResponse getAllStudentsCount(){
        return analyticsService.getAllStudentsCount();
    }
    @GetMapping("getAllCoursesCount")
    @Operation(summary = "Получить данные курса",description = "Получить количество курса,инструктора и группы!" +
            "Авторизация:  Админ!")
    public CourseAnalyticsResponse getAllCoursesCount(){
        return analyticsService.getAllCoursesCount();
    }
}
