package lms.service.impl;

import jakarta.transaction.Transactional;
import lms.dto.request.CourseRequest;
import lms.dto.response.SimpleResponse;
import lms.dto.response.FindAllResponseCourse;
import lms.dto.response.CourseResponse;
import lms.dto.response.AllInstructorsOrStudentsOfCourse;
import lms.dto.response.InstructorsOrStudentsOfCourse;
import lms.entities.Course;
import lms.entities.Trash;
import lms.entities.Group;
import lms.entities.User;
import lms.entities.Instructor;
import lms.entities.Student;
import lms.enums.Role;
import lms.enums.Type;
import lms.exceptions.AlreadyExistsException;
import lms.exceptions.BadRequestException;
import lms.exceptions.IllegalArgumentException;
import lms.exceptions.NotFoundException;
import lms.repository.CourseRepository;
import lms.repository.GroupRepository;
import lms.repository.InstructorRepository;
import lms.repository.TrashRepository;
import lms.repository.StudentRepository;
import lms.repository.UserRepository;
import lms.service.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CourseServiceImpl implements CourseService {
    private final CourseRepository courseRepository;
    private final GroupRepository groupRepository;
    private final InstructorRepository instructorRepository;
    private final StudentRepository studentRepository;
    private final TrashRepository trashRepository;
    private final UserRepository userRepository;

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
    @Transactional
    public SimpleResponse deleteCourseById(Long courseId) {
        Course course = courseRepository.findById(courseId).orElseThrow(()
                -> new NotFoundException("Курс с id: " + courseId + " не существует!"));
        Trash trash = new Trash();
        course.setTrash(trash);
        trash.setName(course.getTitle());
        trash.setType(Type.COURSE);
        trash.setDateOfDelete(ZonedDateTime.now());
        trashRepository.save(trash);
        return SimpleResponse.builder()
                .httpStatus(HttpStatus.OK)
                .message("Успешно удалено!")
                .build();
    }

    @Override
    public FindAllResponseCourse findAllCourse(int page, int size) {
        Pageable pageable = getPageable(page,size);

        Page<CourseResponse> allCourse = courseRepository.findAllCourse(pageable);
        return FindAllResponseCourse.builder()
                .page(allCourse.getNumber() + 1)
                .size(allCourse.getNumberOfElements())
                .courses(allCourse.getContent())
                .build();

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
    @Transactional
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
    @Transactional
    public AllInstructorsOrStudentsOfCourse findAllInstructorsOrStudentsByCourseId(int page, int size, Long courseId, Role role) {
        Pageable pageable = getPageable(page, size);
        Course course = courseRepository.findById(courseId).orElseThrow(
                () -> new NotFoundException("Курс с Id:   " + courseId + "  не найдены.")
        );
        if (role.equals(Role.STUDENT)) {
            Page<InstructorsOrStudentsOfCourse> allStudentByCourseId = studentRepository.getStudentsByCourseId(course.getId(), pageable);
            return AllInstructorsOrStudentsOfCourse.builder()
                    .page(allStudentByCourseId.getNumber() + 1)
                    .size(allStudentByCourseId.getNumberOfElements())
                    .getAllInstructorsOfCourses(allStudentByCourseId.getContent())
                    .build();
        } else if (role.equals(Role.INSTRUCTOR)) {
            Page<InstructorsOrStudentsOfCourse> allInstructorsByCourseId = instructorRepository.getInstructorsByCourseId(courseId, pageable);
            return AllInstructorsOrStudentsOfCourse.builder()
                    .page(allInstructorsByCourseId.getNumber() + 1)
                    .size(allInstructorsByCourseId.getNumberOfElements())
                    .getAllInstructorsOfCourses(allInstructorsByCourseId.getContent())
                    .build();
        }
        throw new NotFoundException("Курс с Id:  " + courseId + " не найдены.");
    }

    @Override
    public FindAllResponseCourse findMyCourse(int page, int size) {
        Pageable pageable = getPageable(page, size);
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.getByEmail(email);
        if (currentUser.getRole().equals(Role.INSTRUCTOR)) {
            Instructor instructor = instructorRepository.findByUserId(currentUser.getId()).orElseThrow(() -> new NotFoundException("Инструктор не найден!!!"));
           Page<CourseResponse> instructorCourses =  courseRepository.findByInstructorId(instructor.getId(), pageable);
            return FindAllResponseCourse.builder()
                    .page(instructorCourses.getNumber() + 1)
                    .size(instructorCourses.getNumberOfElements())
                    .courses(instructorCourses.getContent())
                    .build();
        } else {
            Student student = studentRepository.findStudentByUserId(currentUser.getId()).orElseThrow(() -> new NotFoundException("Студент не найден!!!"));
            Page<CourseResponse> studentCourses = courseRepository.findByStudentId(student.getId(), pageable);
            return FindAllResponseCourse.builder()
                    .page(studentCourses.getNumber() + 1)
                    .size(studentCourses.getNumberOfElements())
                    .courses(studentCourses.getContent())
                    .build();
        }
    }

    private Pageable getPageable(int page, int size){
        if (page < 1 && size < 1) throw new BadRequestException("Page - size  страницы должен быть больше 0.");
       return PageRequest.of(page - 1, size);
    }
}



