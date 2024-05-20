package lms.api;

import jakarta.validation.Valid;
import lms.dto.request.VideoRequest;
import lms.dto.response.AllVideoResponse;
import lms.dto.response.SimpleResponse;
import lms.dto.response.VideoResponse;
import lms.service.VideoService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/videos")
@RequiredArgsConstructor
@CrossOrigin(origins = "*",maxAge = 3600)
public class VideoApi {
    private final VideoService videoService;

    @Secured("INSTRUCTOR")
    @PostMapping("/save/{lessonId}")
    public SimpleResponse save(@RequestBody @Valid VideoRequest videoRequest, @PathVariable Long lessonId) {
        return videoService.save(videoRequest, lessonId);
    }

    @Secured("INSTRUCTOR")
    @PutMapping("/update/{videoId}")
    public SimpleResponse update(@PathVariable Long videoId, @RequestBody @Valid VideoRequest videoRequest) {
        return videoService.update(videoId, videoRequest);
    }

    @Secured("INSTRUCTOR")
    @GetMapping("/findAll/{lessonId}")
    public AllVideoResponse findAll(@RequestParam int page, @RequestParam int size, @PathVariable Long lessonId) {
        return videoService.findAllVideoByLessonId(size, page, lessonId);
    }

    @PreAuthorize("hasAnyAuthority('INSTRUCTOR', 'STUDENT')")
    @GetMapping("/findById/{videoId}")
    public VideoResponse findById(@PathVariable Long videoId) {
        return videoService.findById(videoId);
    }


    @Secured("INSTRUCTOR")
    @DeleteMapping("/delete/{videoId}")
    public SimpleResponse delete(@PathVariable Long videoId) {
        return videoService.delete(videoId);
    }

}
