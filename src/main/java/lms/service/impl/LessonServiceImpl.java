package lms.service.impl;

import jakarta.transaction.Transactional;
import lms.dto.request.LessonRequest;
import lms.dto.response.SimpleResponse;
import lms.dto.response.LessonResponse;
import lms.dto.response.AllLessonsResponse;
import lms.entities.Lesson;
import lms.entities.Notification;
import lms.entities.Task;
import lms.entities.Course;
import lms.exceptions.NotFoundException;
import lms.repository.CourseRepository;
import lms.repository.LessonRepository;
import lms.repository.VideoRepository;
import lms.repository.NotificationRepository;
import lms.repository.PresentationRepository;
import lms.repository.LinkRepository;
import lms.repository.TaskRepository;
import lms.repository.TestRepository;
import lms.service.LessonService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
@Service
@RequiredArgsConstructor
@Validated
@Slf4j
public class LessonServiceImpl implements LessonService {
    private final CourseRepository courseRepository;
    private final LessonRepository lessonRepository;
    private final NotificationRepository notificationRepository;
    private final TaskRepository taskRepository;
    private final PresentationRepository presentationRepository;
    private final VideoRepository videoRepository;
    private final LinkRepository linkRepository;
    private final TestRepository testRepository;

    @Override
    public SimpleResponse addLesson(LessonRequest lessonRequest, Long courseId) {
        Course course = courseRepository.findById(courseId).orElseThrow(() -> new NotFoundException("course not found"));
        Lesson lesson = new Lesson();
        lesson.setCourse(course);
        lesson.setTitle(lessonRequest.getTitle());
        lessonRepository.save(lesson);
        return SimpleResponse.builder()
                .httpStatus(HttpStatus.OK)
                .message("успешно сохранён")
                .build();
    }

    @Override
    public AllLessonsResponse findAll(int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<LessonResponse> allLessons = lessonRepository.findAllLessons(pageable);
        return AllLessonsResponse.builder()
                .page(allLessons.getNumber() + 1)
                .size(allLessons.getSize())
                .lessonResponses(allLessons.getContent())
                .build();
    }

    @Override
    public LessonResponse findById(Long lessonId) {
        Lesson lesson = lessonRepository.findById(lessonId).orElseThrow(() -> new NotFoundException("lesson not found"));
        return LessonResponse.builder()
                .id(lesson.getId())
                .title(lesson.getTitle())
                .build();
    }

    @Override
    @Transactional
    public SimpleResponse update(LessonRequest lessonRequest, Long lessonId) {
        Lesson lesson = lessonRepository.findById(lessonId).orElseThrow(() -> new NotFoundException("lesson not found"));
        lesson.setTitle(lessonRequest.getTitle());
        return SimpleResponse.builder()
                .httpStatus(HttpStatus.OK)
                .message("успешно обнолён")
                .build();
    }

    @Override
    @Transactional
    public SimpleResponse delete(Long lessonId) {
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new NotFoundException("Урок не найден"));
        lesson.getPresentations().clear();
        lesson.getVideos().clear();
        lesson.getLinks().clear();
        lesson.setTest(null);
        presentationRepository.deleteAll(lesson.getPresentations());
        videoRepository.deleteAll(lesson.getVideos());
        linkRepository.deleteAll(lesson.getLinks());
        if (lesson.getTest() != null) {
            testRepository.delete(lesson.getTest());
        }
        for (Task task : lesson.getTasks()) {
            Notification notification = notificationRepository.findByTaskId(task.getId());
                notification.setTask(null);
            taskRepository.deleteById(task.getId());
        }
        lessonRepository.delete(lesson);
        return SimpleResponse.builder()
                .httpStatus(HttpStatus.OK)
                .message("Урок и связанные задачи успешно удалены")
                .build();
    }
}

