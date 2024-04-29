package lms.service;

import lms.dto.request.LessonRequest;
import lms.dto.response.AllLessonsResponse;
import lms.dto.response.LessonResponse;
import lms.dto.response.SimpleResponse;

public interface LessonService {
    SimpleResponse addLesson(LessonRequest lessonRequest, Long courseId);

    AllLessonsResponse findAll(int page, int size, Long courseId);

    LessonResponse findById(Long lessonId);

    SimpleResponse update(LessonRequest lessonRequest, Long lessonId);

    SimpleResponse delete(Long lessonId);
}
