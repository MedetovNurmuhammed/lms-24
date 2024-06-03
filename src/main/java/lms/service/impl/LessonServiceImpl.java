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
import org.springframework.data.domain.Sort;
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
        Course course = courseRepository.findCourseById(courseId).orElseThrow(() -> new NotFoundException("Курс c " + courseId + " не найден"));
        if (course.getTrash() == null) {
            Lesson lesson = new Lesson();
            lesson.setCourse(course);
            lesson.setTitle(lessonRequest.getTitle());
            lesson.setCreatedAt(lessonRequest.getCreatedAt());
            lessonRepository.save(lesson);
            return SimpleResponse.builder()
                    .httpStatus(HttpStatus.OK)
                    .message("Урок " + lesson.getTitle() + " успешно сохранено")
                    .build();
        } else throw new BadRequestException("Курс может быть в корзине!")
    }

    @Override
    public AllLessonsResponse findAll(int page, int size, Long courseId) {
        if (page < 1 && size < 1)
            throw new java.lang.IllegalArgumentException("Индекс страницы не должен быть меньше нуля");
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("id"));
        Page<LessonResponse> allLessons = lessonRepository.findAllLessons(courseId, pageable);
        return AllLessonsResponse.builder()
                .page(allLessons.getNumber() + 1)
                .size(allLessons.getNumberOfElements())
                .lessonResponses(allLessons.getContent())
                .build();
    }

    @Override
    public LessonResponse findById(Long lessonId) {
        Lesson lesson = lessonRepository.findLessonById(lessonId).orElseThrow(() -> new NotFoundException("Урок c " + lessonId + " не найден"));
        if (lesson.getTrash() == null) {
            return LessonResponse.builder()
                    .id(lesson.getId())
                    .title(lesson.getTitle())
                    .createdAt(lesson.getCreatedAt())
                    .build();
        } else throw new BadRequestException("Урок может быть в корзине!");
    }

    @Override
    @Transactional
    public SimpleResponse update(LessonRequest lessonRequest, Long lessonId) {
        Lesson lesson = lessonRepository.findLessonById(lessonId).orElseThrow(() -> new NotFoundException("Урок c " + lessonId + " не найден"));
        if (lesson.getTrash() == null) {
            lesson.setTitle(lessonRequest.getTitle());
            return SimpleResponse.builder()
                    .httpStatus(HttpStatus.OK)
                    .message("урок " + lesson.getTitle() + " успешно обновлён")
                    .build();
        } else throw new BadRequestException("Урок может быть в корзине!");
    }

    @Override
    @Transactional
    public SimpleResponse delete(Long lessonId) {
        Lesson lesson = lessonRepository.findLessonById(lessonId)
                .orElseThrow(() -> new NotFoundException("Урок c " + lessonId + " не найден"));
        if (lesson.getTrash() == null) {
            Trash trash = new Trash();
            lesson.setTrash(trash);
            trash.setName(lesson.getTitle());
            trash.setType(Type.LESSON);
            trash.setDateOfDelete(ZonedDateTime.now());
            trashRepository.save(trash);
            return SimpleResponse.builder()
                    .httpStatus(HttpStatus.OK)
                    .message("Урок и связанные задачи успешно добавлено в корзину!")
                    .build();
        }else throw new BadRequestException("Урок может быть в корзине!");
    }

    private Pageable getPageable(int page, int size) {
        if (page < 1 && size < 1) throw new BadRequestException("Page - size  страницы должен быть больше 0.");
        return PageRequest.of(page - 1, size);
    }
}

