package lms.service.impl;

import jakarta.transaction.Transactional;
import lms.dto.request.CourseRequest;
import lms.dto.response.SimpleResponse;
import lms.dto.response.FindAllResponseCourse;
import lms.dto.response.AllInstructorsOrStudentsOfCourse;
import lms.dto.response.InstructorsOrStudentsOfCourse;
import lms.entities.Course;
import lms.entities.Group;
import lms.entities.Instructor;
import lms.enums.Role;
import lms.exceptions.AlreadyExistsException;
import lms.exceptions.IllegalArgumentException;
import lms.exceptions.NotFoundException;
import lms.repository.CourseRepository;
import lms.repository.GroupRepository;
import lms.repository.InstructorRepository;
import lms.repository.StudentRepository;
import lms.service.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
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
    private final GroupRepository groupRepository;
    private final InstructorRepository instructorRepository;
    private final StudentRepository studentRepository;

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
        Course course = courseRepository.findById(courseId).orElseThrow(()
                -> new NotFoundException("Курс с id: " + courseId + " не существует!"));
        List<Instructor> instructors = course.getInstructors();
        for (Instructor instructor : instructors) {
            instructor.setCourses(null);
        }
        course.setInstructors(null);
        courseRepository.deleteById(course.getId());
        return SimpleResponse.builder()
                .httpStatus(HttpStatus.OK)
                .message("Успешно удалено!")
                .build();
    }

    @Override
    public Page<FindAllResponseCourse> findAllCourse(int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);

        return courseRepository.findAllCourse(pageable);
    }

    @Transactional
    @Override
    public SimpleResponse assignInGroupToCourse(Long groupId, Long courseId) {
        Group group = groupRepository.findById(groupId).orElseThrow(() -> new NotFoundException("Группа с id: " + groupId + " не найден!"));
        Course course = courseRepository.findById(courseId).orElseThrow(() -> new NotFoundException("Курс с id: " + courseId + " не найден!"));
        group.getCourses().add(course);
        course.getGroups().add(group);
        courseRepository.save(course);
        groupRepository.save(group);
        return SimpleResponse.builder()
                .httpStatus(HttpStatus.OK)
                .message("Успешно добавлено!")
                .build();
    }

    @Override
    public SimpleResponse assignInstructorsToCourse(Long courseId, List<Long> instructorIds) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new NotFoundException("Курс с id: " + courseId + " не существует!"));

        List<Instructor> foundInstructors = instructorRepository.findAllById(instructorIds);
        List<Long> notFoundInstructorIds = new ArrayList<>();
        List<Long> existingInstructorIds = new ArrayList<>();
        List<Long> addedInstructorIds = new ArrayList<>();
        boolean allInstructorsExist = true;

        for (Long instructorId : instructorIds) {
            boolean found = false;
            for (Instructor instructor : foundInstructors) {
                if (instructor.getId().equals(instructorId)) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                notFoundInstructorIds.add(instructorId);
                allInstructorsExist = false;
            }
        }

        if (allInstructorsExist) {
            for (Instructor instructor : foundInstructors) {
                if (!course.getInstructors().contains(instructor)) {
                    course.getInstructors().add(instructor);
                    instructor.getCourses().add(course);
                    instructorRepository.save(instructor);
                    addedInstructorIds.add(instructor.getId());
                } else {
                    existingInstructorIds.add(instructor.getId());
                }
            }
            courseRepository.save(course);

            StringBuilder messageBuilder = new StringBuilder("Инструкторы успешно добавлены!");
            if (!existingInstructorIds.isEmpty()) {
                messageBuilder.append(" Инструкторы с id: ").append(existingInstructorIds).append(" уже привязаны к этому курсу.");
            }
            if (!addedInstructorIds.isEmpty()) {
                messageBuilder.append(" Добавлены инструкторы с id: ").append(addedInstructorIds);
            }
            return SimpleResponse.builder()
                    .httpStatus(HttpStatus.OK)
                    .message(messageBuilder.toString())
                    .build();
        } else {
            StringBuilder errorMessageBuilder = new StringBuilder("Инструкторы с предоставленными  id: ");
            errorMessageBuilder.append(notFoundInstructorIds).append(" не найдены.");
            return SimpleResponse.builder()
                    .httpStatus(HttpStatus.NOT_FOUND)
                    .message(errorMessageBuilder.toString())
                    .build();
        }
    }

    @Override
    public AllInstructorsOrStudentsOfCourse findAllInstructorsOrStudentsByCourseId(int page, int size, Long courseId, Role role) {
        Pageable pageable = PageRequest.of(page - 1, size);
        if (role.equals(Role.STUDENT)) {
            Page<InstructorsOrStudentsOfCourse> allStudentByCourseId = studentRepository.getStudentsByCourseId(courseId, pageable);
            return AllInstructorsOrStudentsOfCourse.builder()
                    .page(allStudentByCourseId.getNumber() + 1)
                    .size(allStudentByCourseId.getSize())
                    .getAllInstructorsOfCourses(allStudentByCourseId.getContent())
                    .build();
        } else if (role.equals(Role.INSTRUCTOR)) {
            Page<InstructorsOrStudentsOfCourse> allInstructorsByCourseId = instructorRepository.findAllInstructorsByCourseId(courseId, pageable);
            if (allInstructorsByCourseId.getContent().isEmpty()) {
                return AllInstructorsOrStudentsOfCourse.builder()
                        .page(allInstructorsByCourseId.getNumber() + 1)
                        .size(allInstructorsByCourseId.getSize())
                        .getAllInstructorsOfCourses(allInstructorsByCourseId.getContent())
                        .build();
            } else {
                return AllInstructorsOrStudentsOfCourse.builder()
                        .page(allInstructorsByCourseId.getNumber() + 1) // Adjusted to 1-index
                        .size(allInstructorsByCourseId.getSize())
                        .getAllInstructorsOfCourses(allInstructorsByCourseId.getContent())
                        .build();
            }
        } else {
            throw new NotFoundException("Course not found or role is null");
        }
    }

}



