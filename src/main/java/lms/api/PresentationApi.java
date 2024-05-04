package lms.api;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lms.dto.request.EditPresentationRequest;
import lms.dto.request.PresentationRequest;
import lms.dto.response.FindAllPresentationResponse;
import lms.dto.response.PresentationResponse;
import lms.dto.response.SimpleResponse;
import lms.service.PresentationService;
import lms.service.impl.PresentationServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/presentation")
public class PresentationApi {
    private final PresentationService presentationService;
    private final PresentationServiceImpl presentationService;

    @Secured("INSTRUCTOR")
    @Operation(description = "create presentation")
    @PostMapping(value = "/createPresentation/{lessonId}")
    public SimpleResponse createPresentation(@PathVariable Long lessonId, @RequestBody @Valid PresentationRequest presentationRequest) {
        return presentationService.createPresentation(lessonId, presentationRequest);
    }

    @Secured("INSTRUCTOR")
    @Operation(description = "Update Presentation")
    @PatchMapping(value = "updatePresentation/{presentationId}")
    public SimpleResponse editPresentation(@PathVariable Long presentationId, @RequestBody EditPresentationRequest editPresentationRequest) {
        return presentationService.editPresentation(presentationId, editPresentationRequest);
    }

    @Secured({"INSTRUCTOR", "STUDENT"})
    @GetMapping(value = "findById/{presentationId}")
    @Operation(description = "find By presentation")
    public PresentationResponse findById(@PathVariable Long presentationId) {
        return presentationService.findById(presentationId);
    }


}

@Secured({"INSTRUCTOR", "STUDENT"})
@Operation(description = "Возвращает все презентации урока по id")
@GetMapping("findAll presentation/{lessonId}")
public FindAllPresentationResponse findAll(@RequestParam(required = false, defaultValue = "1") int page,
                                           @RequestParam(required = false, defaultValue = "6") int size,
                                           @PathVariable Long lessonId) {
    return presentationService.findAllPresentationByLessonId(page, size, lessonId);
}

@Operation(summary = "Удалить презентацию",
        description = "Метод для удаления презентацию по его идентификатору." +
                " Авторизация:  инструктор!")
@PreAuthorize("hasAnyAuthority('ADMIN','INSTRUCTOR')")
@DeleteMapping("/delete/{presentationId}")
public SimpleResponse delete(@PathVariable Long presentationId) {
    return presentationService.delete(presentationId);
}
