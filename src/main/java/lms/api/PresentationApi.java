package lms.api;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lms.dto.request.EditPresentationRequest;
import lms.dto.request.PresentationRequest;
import lms.dto.response.PresentationResponse;
import lms.dto.response.SimpleResponse;
import lms.service.PresentationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/presentation")
public class PresentationApi {
    private final PresentationService presentationService;

    @Operation(description = "create presentation")
    @PostMapping(value = "/createPresentation/{lessonId}")
    public SimpleResponse createPresentation(@PathVariable Long lessonId, PresentationRequest presentationRequest, @RequestPart("file") @Valid MultipartFile file) {
        return presentationService.createPresentation(lessonId, presentationRequest, file);
    }
    @Operation(description = "Update Presentation")
    @PatchMapping(value = "updatePresentation/{presentationId}")
    public SimpleResponse editPresentation(@PathVariable Long presentationId, EditPresentationRequest editPresentationRequest, @RequestPart(value = "file", required = false) @Valid MultipartFile file
    ) {
        return presentationService.editPresentation(presentationId, editPresentationRequest, file);
    }
    @GetMapping(value = "findById/{presentationId}")
    @Operation(description = "find By presentation")
    public PresentationResponse findById(@PathVariable Long presentationId){
        return presentationService.findById(presentationId);
    }
    @DeleteMapping(value = "delete/{presentationId}")
    @Operation(description = "delete presentation by id")
    public SimpleResponse deleteById(@PathVariable Long presentationId){
        return  presentationService.deletePresentationById(presentationId);
    }
}
