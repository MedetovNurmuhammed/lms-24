package lms.api;

import io.swagger.v3.oas.annotations.Operation;
import lms.dto.response.SimpleResponse;
import lms.service.impl.PresentationServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/presentation")
@RestController
@RequiredArgsConstructor
public class PresentationApi {
    private final PresentationServiceImpl presentationService;

    @Operation(summary = "Удалить презентацию",
            description = "Метод для удаления презентацию по его идентификатору." +
                    " Авторизация:  инструктор!")
    @PreAuthorize("hasAnyAuthority('ADMIN','INSTRUCTOR')")
    @DeleteMapping("/delete/{presentationId}")
    public SimpleResponse delete(@PathVariable Long presentationId) {
        return presentationService.delete(presentationId);
    }
}
