package lms.api;

import io.swagger.v3.oas.annotations.Operation;
import lms.dto.response.SimpleResponse;
import lms.service.VideoService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/video")
@RequiredArgsConstructor
public class VideoApi {
    private final VideoService videoService;

    @Operation(summary = "Удалить видеоурока",
            description = "Метод для удаления видеоурока по его идентификатору." +
                    " Авторизация:  инструктор!")
    @PreAuthorize("hasAnyAuthority('ADMIN','INSTRUCTOR')")
    @DeleteMapping("/delete/{studId}")
    public SimpleResponse delete(@PathVariable Long studId) {
        return videoService.delete(studId);
    }

}
