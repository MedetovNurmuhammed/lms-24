package lms.api;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lms.dto.request.LessonRequest;
import lms.dto.response.AllLessonsResponse;
import lms.dto.response.LessonResponse;
import lms.dto.response.SimpleResponse;
import lms.service.LessonService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/lessons")
public class LessonApi {
    private final LessonService lessonService;
    @Secured("INSTRUCTOR")
    @Operation(summary = "добавляет урок.(Авторизация: инструктор)")
    @PostMapping("/addLesson/{courseId}")
    public SimpleResponse addLesson(@RequestBody @Valid LessonRequest lessonRequest, @PathVariable Long courseId) throws MessagingException {
        return lessonService.addLesson(lessonRequest, courseId);
    }

    @Secured("INSTRUCTOR")
    @Operation(summary = "Возвращает пагинированный список всех уроков.(Авторизация: инструктор)")
    @GetMapping("/findAll/{courseId}")
    public AllLessonsResponse findAll(@RequestParam(required = false, defaultValue = "1") int page,
                                      @RequestParam(required = false, defaultValue = "6") int size, @PathVariable Long courseId) {
        return lessonService.findAll(page, size,courseId);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @Operation(summary = "Возвращает урок.(Авторизация: инструктор)")
    @GetMapping("/findById/{lessonId}")
    public LessonResponse findById(@PathVariable Long lessonId) {
        return lessonService.findById(lessonId);
    }

    @Secured("INSTRUCTOR")
    @PutMapping("/updateLesson/{lessonId}")
    @Operation(summary = "Обновляет информацию о уроке.(Авторизация: инструктор)")
    public SimpleResponse update(
            @RequestBody @Valid LessonRequest lessonRequest, @PathVariable Long lessonId) {
        return lessonService.update(lessonRequest, lessonId);
    }

    @Operation(summary = "Удаляет текущий урок.(Авторизация: инструктор)")
    @DeleteMapping("/delete/{lessonId}")
    public SimpleResponse delete(@PathVariable Long lessonId) {
        return lessonService.delete(lessonId);
    }
}
