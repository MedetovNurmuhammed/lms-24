package lms.service.impl;

import jakarta.transaction.Transactional;
import lms.dto.request.CourseRequest;
import lms.dto.response.FindAllResponseCourse;
import lms.dto.response.SimpleResponse;
import lms.entities.Course;
import lms.entities.Group;
import lms.entities.Instructor;
import lms.exceptions.AlreadyExistsException;
import lms.exceptions.IllegalArgumentException;
import lms.exceptions.NotFoundException;
import lms.repository.CourseRepository;
import lms.repository.GroupRepository;
import lms.repository.InstructorRepository;
import lms.service.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CourseServiceImpl implements CourseService {
    private final CourseRepository courseRepository;
    private final GroupRepository groupRepository;//очуруш керек созсуз
    private final InstructorRepository instructorRepository;//очуруш керек созсуз

    private void checkTitle(String courseTitle) {
        boolean exists = courseRepository.existsByTitle(courseTitle);
        if (exists) throw new AlreadyExistsException("Курс с названием: " + courseTitle + " уже существует!");
    }

    @Transactional
    @Override
    public SimpleResponse createCourse(CourseRequest courseRequest) {
        checkTitle(courseRequest.getTitle());
        Course course = new Course();
        course.setImage(courseRequest.getImage());
        course.setTitle(courseRequest.getTitle());
        course.setDescription(courseRequest.getDescription());
        course.setDateOfEnd(courseRequest.getDateOfEnd());
        course.setDateOfStart(LocalDate.now());
        if (courseRequest.getDateOfEnd() != null && courseRequest.getDateOfEnd().isBefore(course.getDateOfStart())) {
            throw new IllegalArgumentException("Дата окончания курса не может быть раньше даты его начала.");
        }
        courseRepository.save(course);

        return SimpleResponse.builder()
                .httpStatus(HttpStatus.CREATED)
                .message("Курс успешно добавлен!")
                .build();
    }

    @Transactional
    @Override
    public SimpleResponse editCourse(Long courseId,
                                     CourseRequest courseRequest) {
        Course course = courseRepository.findById(courseId).orElseThrow(() -> new NotFoundException("Курс с id :" + courseId + " не найден!"));
        checkTitle(courseRequest.getTitle());
        course.setTitle(courseRequest.getTitle());
        course.setImage(courseRequest.getImage());
        course.setDescription(courseRequest.getDescription());
        course.setDateOfEnd(courseRequest.getDateOfEnd());
        courseRepository.save(course);
        return SimpleResponse.builder()
                .httpStatus(HttpStatus.OK)
                .message("Успешно изменено!")
                .build();
    }

    @Override
    public SimpleResponse deleteCourseById(Long courseId) {
        Course course = courseRepository.findById(courseId).orElseThrow(() -> new NotFoundException("Курс с id: " + courseId + " не существует!"));
        courseRepository.deleteById(course.getId());
        return SimpleResponse.builder()
                .httpStatus(HttpStatus.OK)
                .message("Успешно удалено!")
                .build();
    }

    @Override
    public Page<FindAllResponseCourse> findAllCourse(int page,int size) {
        Pageable pageable = PageRequest.of(page - 1, size);

        return courseRepository.findAllCourse(pageable);
    }

    @Transactional
    @Override
    public SimpleResponse assignInGroupToCourse(Long groupId, Long courseId) {
        Group group = groupRepository.findById(groupId).orElseThrow(() -> new NotFoundException("Группа с id: " + groupId + " не найден!"));
        Course course = courseRepository.findById(courseId).orElseThrow(() -> new NotFoundException("Курс с id: " + courseId + " не найден!"));
        group.getCourses().add(course);
        course.setGroup(group);
        courseRepository.save(course);
        groupRepository.save(group);
        return SimpleResponse.builder()
                .httpStatus(HttpStatus.OK)
                .message("Успешно добавлено!")
                .build();
    }

    @Transactional
    @Override
    public SimpleResponse assignInInstructorToCourse(Long courseId, Long instructorId) {
        Course course = courseRepository.findById(courseId).orElseThrow(() -> new NotFoundException("Курс с id: " + courseId + " не существует!"));
        Instructor instructor = instructorRepository.findById(instructorId).orElseThrow(() -> new NotFoundException("Инструктор с id: " + instructorId + " не существует!"));
        course.getInstructors().add(instructor);
        instructor.setCourse(course);
        instructorRepository.save(instructor);
        courseRepository.save(course);
        return SimpleResponse.builder()
                .httpStatus(HttpStatus.OK)
                .message("Инструктор успешно добавлен!")
                .build();
    }

}
