package lms.service;

import lms.dto.request.VideoRequest;
import lms.dto.response.AllVideoResponse;
import lms.dto.response.SimpleResponse;
import lms.dto.response.VideoResponse;

public interface VideoService {
    SimpleResponse save(VideoRequest videoRequest, Long lessonId);

    SimpleResponse update(Long videoId, VideoRequest videoRequest);

    AllVideoResponse findAllVideoByLessonId(int size, int page, Long lessonId);

    VideoResponse findById(Long videoId);

    SimpleResponse delete(Long videoId);
}
