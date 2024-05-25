package lms.api;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lms.dto.request.AnnouncementRequest;
import lms.dto.response.*;
import lms.service.AnnouncementService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/announcement")
@RequiredArgsConstructor
@CrossOrigin(origins = "*",maxAge = 3600)
public class AnnouncementApi {
    private final AnnouncementService announcementService;

    @Secured({"ADMIN", "INSTRUCTOR"})
    @Operation(summary = "Создание объявления",description = "Создание нового объявления с указанными данными.Авторизация:админ и инструктор")
    @PostMapping()
    public SimpleResponse create(@RequestBody @Valid AnnouncementRequest announcementRequest) {
        return announcementService.createAnnouncement(announcementRequest);
    }

    @Secured({"ADMIN", "INSTRUCTOR", "STUDENT"})
    @Operation(summary = "Просмотр объявления по идентификатору", description = "Получение информации об объявлении " +
            "по его идентификатору.Авторизация:админ , инструктор и студент")
    @PutMapping("view/{announcementId}")
    public AnnouncementResponse viewAnnouncementById(@PathVariable("announcementId") Long announcementId) {
        return announcementService.viewAnnouncement(announcementId);
    }

    @Secured({"ADMIN", "INSTRUCTOR"})
    @Operation(summary = "Публикация объявления",description = "Обновление статуса публикации объявления по его идентификатору.Авторизация:админ и инструктор")
    @PutMapping("/{announcementId}")
    public SimpleResponse isPublished(@PathVariable Long announcementId, @RequestParam Boolean isPublished) {
        return announcementService.isPublished(announcementId, isPublished);
    }

    @Secured({"ADMIN", "INSTRUCTOR"})
    @Operation(summary = "Поиск всех объявлений по идентификатору группы", description = "Получение всех объявлений для указанной.Авторизация:админ и инструктор " +
            "группы с возможностью пагинации")
    @GetMapping("/search")
    public AllAnnouncementResponse findAllAnnouncementByGroupId(
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "4") int size,
            @RequestParam(required = false) Long groupId) {
        return announcementService.findAllAnnouncementByGroupId(page, size, groupId);
    }

    @Secured("STUDENT")
    @Operation(summary = "Поиск всех объявлений для студента", description = "Получение всех объявлений, предназначенных для студента, с возможностью пагинации.Авторизация:студент")
    @GetMapping()
    public AllAnnouncementOfStudentResponse findAllByGroupId(
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "4") int size,
            @RequestParam(required = false, defaultValue = "false") Boolean isView ) {
        return announcementService.allAnnouncementOfStudent(page, size, isView);
    }

    @Secured({"INSTRUCTOR", "ADMIN"})
    @Operation(summary = "Удаление объявления по идентификатору",
            description = "Удаляет объявление из системы по его уникальному идентификатору.Авторизация:админ и инструктор")
    @DeleteMapping("/{announcementId}")
    public SimpleResponse delete(@PathVariable Long announcementId) {
        return announcementService.deleteById(announcementId);
    }

    @Secured({"ADMIN", "INSTRUCTOR"})
    @Operation(summary = "Обновление объявления по идентификатору",
            description = "Обновляет информацию об объявлении в системе на основе его уникального идентификатора.Авторизация:админ и инструктор")
    @PatchMapping("/{announcementId}")
    public SimpleResponse update(@PathVariable Long announcementId,
                                 @RequestBody  AnnouncementRequest announcementRequest) {
        return announcementService.update(announcementId, announcementRequest);
    }
}
