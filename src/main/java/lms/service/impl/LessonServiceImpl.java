package lms.service.impl;

import jakarta.transaction.Transactional;
import lms.dto.request.LessonRequest;
import lms.dto.response.SimpleResponse;
import lms.dto.response.LessonResponse;
import lms.dto.response.AllLessonsResponse;
import lms.entities.Lesson;
import lms.entities.Course;
import lms.entities.Trash;
import lms.enums.Type;
import lms.exceptions.BadRequestException;
import lms.exceptions.NotFoundException;
import lms.repository.CourseRepository;
import lms.repository.LessonRepository;
import lms.repository.TrashRepository;
import lms.service.LessonService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDate;
import java.time.ZonedDateTime;

@Service
@RequiredArgsConstructor
@Validated
@Slf4j
public class LessonServiceImpl implements LessonService {
    private final CourseRepository courseRepository;
    private final LessonRepository lessonRepository;
    private final TrashRepository trashRepository;

    @Override
    public SimpleResponse addLesson(LessonRequest lessonRequest, Long courseId) {
        Course course = courseRepository.findById(courseId).orElseThrow(() -> new NotFoundException("Курс c " + courseId + " не найден"));
        Lesson lesson = new Lesson();
        lesson.setCourse(course);
        lesson.setTitle(lessonRequest.getTitle());
        if (lessonRequest.getCreatedAt().isBefore(LocalDate.now())) {
            throw new BadRequestException("Дата создания не должна быть раньше текущей даты");
        }
        lesson.setCreatedAt(lessonRequest.getCreatedAt());
        lessonRepository.save(lesson);
        return SimpleResponse.builder()
                .httpStatus(HttpStatus.OK)
                .message("урок " + lesson.getTitle() + " успешно сохранено")
                .build();
    }

    @Override
    public AllLessonsResponse findAll(int page, int size, Long courseId) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<LessonResponse> allLessons = lessonRepository.findAllLessons(pageable, courseId);
        return AllLessonsResponse.builder()
                .page(allLessons.getNumber() + 1)
                .size(allLessons.getSize())
                .lessonResponses(allLessons.getContent())
                .build();
    }

    @Override
    public LessonResponse findById(Long lessonId) {
        Lesson lesson = lessonRepository.takeById(lessonId);
        return LessonResponse.builder()
                .id(lesson.getId())
                .title(lesson.getTitle())
                .createdAt(lesson.getCreatedAt())
                .build();
    }

    @Override
    @Transactional
    public SimpleResponse update(LessonRequest lessonRequest, Long lessonId) {
        Lesson lesson = lessonRepository.findById(lessonId).orElseThrow(() -> new NotFoundException("Урок c " + lessonId + " не найден"));
        lesson.setTitle(lessonRequest.getTitle());
        return SimpleResponse.builder()
                .httpStatus(HttpStatus.OK)
                .message("урок " + lesson.getTitle() + " успешно обнолён")
                .build();
    }

    @Override
    @Transactional
    public SimpleResponse delete(Long lessonId) {
        Lesson lesson = lessonRepository.getLessonById(lessonId);
        if (lesson != null) {
            Trash trash = new Trash();
            trash.setName(lesson.getTitle());
            trash.setType(Type.LESSON);
            trash.setDateOfDelete(ZonedDateTime.now());
            trash.setLesson(lesson);
            lesson.setTrash(trash);
            trashRepository.save(trash);
            return SimpleResponse.builder()
                    .httpStatus(HttpStatus.OK)
                    .message("Урок успешно добавлено в корзину")
                    .build();
        }else throw new NotFoundException("Урок не найден!!!");
    }
}

