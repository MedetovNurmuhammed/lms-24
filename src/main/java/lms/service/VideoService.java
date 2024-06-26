package lms.service;

import jakarta.validation.Valid;
import lms.dto.request.VideoRequest;
import lms.dto.response.SimpleResponse;
import lms.dto.response.VideoResponse;
import java.util.List;

public interface VideoService {
    SimpleResponse delete(Long studId);

    SimpleResponse save(@Valid VideoRequest videoRequest, Long lessonId);

    SimpleResponse update(Long videoId, VideoRequest videoRequest);

    List<VideoResponse> findAllVideoByLessonId(Long lessonId);

    VideoResponse findById(Long videoId);

}
