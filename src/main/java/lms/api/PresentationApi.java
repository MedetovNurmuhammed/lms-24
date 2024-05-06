package lms.api;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lms.dto.request.EditPresentationRequest;
import lms.dto.request.PresentationRequest;
import lms.dto.response.FindAllPresentationResponse;
import lms.dto.response.PresentationResponse;
import lms.dto.response.SimpleResponse;
import lms.service.PresentationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/presentation")
public class PresentationApi {
    private final PresentationService presentationService;

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

    @Secured({"INSTRUCTOR"})
    @DeleteMapping(value = "delete/{presentationId}")
    @Operation(description = "Удаляет презинтацию по id")
    public SimpleResponse deleteById(@PathVariable Long presentationId) {
        return presentationService.deletePresentationById(presentationId);
    }

    @Secured({"INSTRUCTOR", "STUDENT"})
    @Operation(description = "Возвращает все презентации урока по id")
    @GetMapping("findAll presentation/{lessonId}")
    public FindAllPresentationResponse findAll(@RequestParam(required = false, defaultValue = "1") int page,
                                               @RequestParam(required = false, defaultValue = "6") int size,
                                               @PathVariable Long lessonId) {
        return presentationService.findAllPresentationByLessonId(page, size, lessonId);
    }
}
