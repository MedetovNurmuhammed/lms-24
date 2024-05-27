package lms.service.impl;

import jakarta.transaction.Transactional;
import lms.dto.request.LessonRequest;
import lms.dto.response.SimpleResponse;
import lms.dto.response.LessonResponse;
import lms.dto.response.AllLessonsResponse;
import lms.entities.*;
import lms.enums.Type;
import lms.exceptions.BadRequestException;
import lms.exceptions.NotFoundException;
import lms.repository.*;
import lms.service.LessonService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
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
    private final UserRepository userRepository;
    private final InstructorRepository instructorRepository;

    @Override
    public SimpleResponse addLesson(LessonRequest lessonRequest, Long courseId) {
        Course course = courseRepository.findById(courseId).orElseThrow(() -> new NotFoundException("Курс c " + courseId + " не найден"));
        Lesson lesson = new Lesson();
        lesson.setCourse(course);
        lesson.setTitle(lessonRequest.getTitle());
        lesson.setCreatedAt(lessonRequest.getCreatedAt());
        lessonRepository.save(lesson);
        return SimpleResponse.builder()
                .httpStatus(HttpStatus.OK)
                .message("Урок " + lesson.getTitle() + " успешно сохранено")
                .build();
    }

    @Override
    public AllLessonsResponse findAll(int page, int size, Long courseId) {
        Pageable pageable = getPageable(page, size);
        Page<LessonResponse> allLessons = lessonRepository.findAllLessons(courseId, pageable);
        return AllLessonsResponse.builder()
                .page(allLessons.getNumber() + 1)
                .size(allLessons.getNumberOfElements())
                .lessonResponses(allLessons.getContent())
                .build();
    }

    @Override
    public LessonResponse findById(Long lessonId) {
        Lesson lesson = lessonRepository.findById(lessonId).orElseThrow(() -> new NotFoundException("Урок c " + lessonId + " не найден"));
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
                .message("урок " + lesson.getTitle() + " успешно обновлён")
                .build();
    }

    @Override
    @Transactional
    public SimpleResponse delete(Long lessonId) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.getByEmail(email);
        Instructor instructor = instructorRepository.findByUserId(currentUser.getId()).
                orElseThrow(() -> new NotFoundException("Инструктор не найден!"));
        Lesson lesson = lessonRepository.getLessonById(lessonId);
            Trash trash = new Trash();
            trash.setName(lesson.getTitle());
            trash.setType(Type.LESSON);
            trash.setDateOfDelete(ZonedDateTime.now());
            trash.setLesson(lesson);
            lesson.setTrash(trash);
            trash.setInstructor(instructor);
            instructor.getTrashes().add(trash);
            trashRepository.save(trash);
            return SimpleResponse.builder()
                    .httpStatus(HttpStatus.OK)
                    .message("Урок успешно добавлено в корзину")
                    .build();

    }

    private Pageable getPageable(int page, int size) {
        if (page < 1 && size < 1) throw new BadRequestException("Page - size  страницы должен быть больше 0.");
        return PageRequest.of(page - 1, size);
    }
}

