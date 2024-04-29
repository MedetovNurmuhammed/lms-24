package lms.api;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lms.dto.response.*;
import lms.service.AnnouncementService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/announcement")
@RequiredArgsConstructor
public class AnnouncementApi {
    private final AnnouncementService announcementService;

    @Secured({"ADMIN", "INSTRUCTOR"})
    @Operation(summary = "Создание объявления")
    @PostMapping("/create")
    public SimpleResponse create(@RequestBody @Valid AnnouncementRequest announcementRequest) {
        return announcementService.createAnnouncement(announcementRequest);
    }

    @Secured({"ADMIN", "INSTRUCTOR", "STUDENT"})
    @Operation(summary = "Просмотр объявления по идентификатору")
    @PutMapping("/viewAnnouncementById/{announcementId}")
    public AnnouncementResponse viewAnnouncementById(@PathVariable("announcementId") long announcementId) {
        return announcementService.viewAnnouncement(announcementId);
    }

    @Secured("ADMIN")
    @Operation(summary = "Получение всех объявлений")
    @GetMapping("/findAllAnnouncement")
    public AllAnnouncementResponse findAllAnnouncement(
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "4") int size
    ) {
        return announcementService.findAll(page, size);
    }

    @Secured({"ADMIN", "INSTRUCTOR"})
    @Operation(summary = "Публикация/снятие с публикации объявления")
    @PutMapping("/isPublished/{announcementId}")
    public SimpleResponse isPublished(@PathVariable Long announcementId, @RequestParam boolean isPublished) {
        return announcementService.isPublished(announcementId, isPublished);
    }

    @Secured({"ADMIN", "INSTRUCTOR"})
    @Operation(summary = "Поиск всех объявлений по идентификатору группы")
    @GetMapping("/findAllAnnouncementByGroupId")
    public AllAnnouncementResponse findAllAnnouncementByGroupId(
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "4") int size,
            @RequestParam(required = false) Long groupId) {
        return announcementService.findAllAnnouncementByGroupId(page,size, groupId);
    }
    @Secured("STUDENT")
    @Operation(summary = "Поиск всех объявлений для студента")
    @GetMapping("/findAllAnnouncementOfStudent")
    public AllAnnouncementOfStudentResponse findAllByGroupId(
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "4") int size,
            @RequestParam(required = false,defaultValue = "false") boolean isView
    ) {
        return announcementService.allAnnouncementOfStudent(page,size,isView);
    }

    @Secured({"INSTRUCTOR", "ADMIN"})
    @Operation(summary = "Удаление объявления по идентификатору")
    @DeleteMapping("/delete/{announcementId}")
    public SimpleResponse delete(@PathVariable long announcementId) {
        return announcementService.deleteById(announcementId);
    }

    @Secured({"ADMIN", "INSTRUCTOR"})
    @Operation(summary = "Обновление объявления по идентификатору")
    @PatchMapping("/update/{announcementId}")
    public SimpleResponse update(@PathVariable long announcementId, @RequestBody @Valid AnnouncementRequest announcementRequest) {
        return announcementService.update(announcementId,announcementRequest);
    }
}
