package lms.service;

import lms.dto.request.VideoRequest;
import lms.dto.response.SimpleResponse;
import lms.dto.response.VideoResponse;
import java.util.List;

public interface VideoService {
    SimpleResponse save(VideoRequest videoRequest, Long lessonId);

    SimpleResponse update(Long videoId, VideoRequest videoRequest);

    List<VideoResponse> findAllVideoByLessonId(Long lessonId);

    VideoResponse findById(Long videoId);

    SimpleResponse delete(Long videoId);
}
