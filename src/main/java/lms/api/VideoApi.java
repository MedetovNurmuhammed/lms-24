package lms.api;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lms.dto.request.VideoRequest;
import lms.dto.response.SimpleResponse;
import lms.dto.response.VideoResponse;
import lms.service.VideoService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;


@RestController
@RequestMapping("/api/videos")
@RequiredArgsConstructor
@CrossOrigin(origins = "*",maxAge = 3600)
public class VideoApi {
    private final VideoService videoService;

    @Secured("INSTRUCTOR")
    @Operation(description = "создать видео")
    @PostMapping("/{lessonId}")
    public SimpleResponse save(@RequestBody @Valid VideoRequest videoRequest, @PathVariable Long lessonId) {
        return videoService.save(videoRequest, lessonId);
    }

    @Secured("INSTRUCTOR")
    @Operation(description = "Обновленить видео")
    @PutMapping("/{videoId}")
    public SimpleResponse update(@PathVariable Long videoId, @RequestBody @Valid VideoRequest videoRequest) {
        return videoService.update(videoId, videoRequest);
    }

    @Secured("INSTRUCTOR")
    @Operation(description = "найти все видео урока")
    @GetMapping("/All/{lessonId}")
    public List<VideoResponse> findAll(
                                    @PathVariable Long lessonId) {
        return videoService.findAllVideoByLessonId(lessonId);
    }

    @PreAuthorize("hasAnyAuthority('INSTRUCTOR', 'STUDENT')")
    @Operation(description = "найти видео по id")
    @GetMapping("/ById/{videoId}")
    public VideoResponse findById(@PathVariable Long videoId) {
        return videoService.findById(videoId);
    }


    @Secured("INSTRUCTOR")
    @Operation(description = "удалить видео по id")
    @DeleteMapping("/{videoId}")
    public SimpleResponse delete(@PathVariable Long videoId) {
        return videoService.delete(videoId);
    }

}
