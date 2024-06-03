package lms.api;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lms.dto.request.EditPresentationRequest;
import lms.dto.request.PresentationRequest;
import lms.dto.response.PresentationResponse;
import lms.dto.response.SimpleResponse;
import lms.service.PresentationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/presentation")
@CrossOrigin(origins = "*",maxAge = 3600)
public class PresentationApi {
    private final PresentationService presentationService;

    @Secured("INSTRUCTOR")
    @Operation(description = "создать презинтацию")
    @PostMapping(value = "/{lessonId}")
    public SimpleResponse createPresentation(@PathVariable Long lessonId, @RequestBody @Valid PresentationRequest presentationRequest) {
        return presentationService.createPresentation(lessonId, presentationRequest);
    }

    @Secured("INSTRUCTOR")
    @Operation(description = "Обновление презинтации")
    @PatchMapping(value = "/{presentationId}")
    public SimpleResponse editPresentation(@PathVariable Long presentationId, @RequestBody EditPresentationRequest editPresentationRequest) {
        return presentationService.editPresentation(presentationId, editPresentationRequest);
    }

    @Secured({"INSTRUCTOR", "STUDENT"})
    @GetMapping(value = "/{presentationId}")
    @Operation(description = "найти презинтацию по id")
    public PresentationResponse findById(@PathVariable Long presentationId) {
        return presentationService.findById(presentationId);
    }

    @Secured({"INSTRUCTOR"})
    @DeleteMapping(value = "/{presentationId}")
    @Operation(description = "Удаляет презинтацию по id")
    public SimpleResponse deleteById(@PathVariable Long presentationId) {
        return presentationService.deletePresentationById(presentationId);
    }

    @Secured({"INSTRUCTOR", "STUDENT"})
    @Operation(description = "Возвращает все презентации урока по id")
    @GetMapping("All/{lessonId}")
    public List<PresentationResponse> findAll(@PathVariable Long lessonId) {
        return presentationService.findAllPresentationByLessonId(lessonId);
    }

    @Operation(summary = "Удалить презентацию",
            description = "Метод для удаления презентацию по его идентификатору." +
                    " Авторизация:  инструктор!")
    @PreAuthorize("hasAnyAuthority('ADMIN','INSTRUCTOR')")
    @DeleteMapping("/delete/{presentationId}")
    public SimpleResponse delete(@PathVariable Long presentationId) {
        return presentationService.deletePresentationById(presentationId);
            }
        }
