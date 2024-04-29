package lms.service;

import lms.dto.response.SimpleResponse;

public interface VideoService {
    SimpleResponse delete(Long studId);

    SimpleResponse save(VideoRequest videoRequest, Long lessonId);

    SimpleResponse update(Long videoId, VideoRequest videoRequest);

    AllVideoResponse findAllVideoByLessonId(int size, int page, Long lessonId);

    VideoResponse findById(Long videoId);

}
