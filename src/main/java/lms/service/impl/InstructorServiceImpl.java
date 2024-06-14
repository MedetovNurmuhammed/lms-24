package lms.service.impl;

import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import lms.dto.request.InstructorRequest;
import lms.dto.request.InstructorUpdateRequest;
import lms.dto.response.SimpleResponse;
import lms.dto.response.InstructorNamesResponse;
import lms.dto.response.AllInstructorResponse;
import lms.dto.response.InstructorResponse;
import lms.dto.response.FindByIdInstructorResponse;
import lms.entities.Instructor;
import lms.entities.User;
import lms.entities.Trash;
import lms.entities.Course;
import lms.enums.Role;
import lms.enums.Type;
import lms.exceptions.AlreadyExistsException;
import lms.exceptions.BadRequestException;
import lms.exceptions.NotFoundException;
import lms.repository.CourseRepository;
import lms.repository.InstructorRepository;
import lms.repository.TrashRepository;
import lms.repository.UserRepository;
import lms.service.InstructorService;
import lms.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Validated
@RequiredArgsConstructor
public class InstructorServiceImpl implements InstructorService {
    private final InstructorRepository instructorRepository;
    private final UserRepository userRepository;
    private final UserService userService;
    private final CourseRepository courseRepository;
    private final TrashRepository trashRepository;

    @Override
    public SimpleResponse addInstructor(InstructorRequest instructorRequest, String linkForPassword) throws MessagingException {
        boolean exists = userRepository.existsByEmail(instructorRequest.getEmail());
        if (exists)
            throw new AlreadyExistsException("Пользователь с электронной почтой " + instructorRequest.getEmail() + " уже существует");

        Instructor instructor = new Instructor();
        User user = new User();
        user.setFullName(instructorRequest.getFirstName() + " " + instructorRequest.getLastName());
        user.setRole(Role.INSTRUCTOR);
        user.setEmail(instructorRequest.getEmail());
        user.setBlock(false);
        user.setPhoneNumber(instructorRequest.getPhoneNumber());
        instructor.setSpecialization(instructorRequest.getSpecialization());
        instructor.setUser(user);
        userRepository.save(user);
        instructorRepository.save(instructor);
        userService.emailSender(user.getEmail(), linkForPassword);
        return SimpleResponse.builder()
                .httpStatus(HttpStatus.OK)
                .message("Инструктор успешно добавлен")
                .build();
    }

    @Override
    public AllInstructorResponse findAll(int page, int size) {
        if(page < 1 && size < 1) throw new BadRequestException("Page - size  страницы должен быть больше 0.");
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("id"));
        Page<InstructorResponse> allInstructors = instructorRepository.findAllInstructors(pageable);
        return AllInstructorResponse.builder()
                .page(allInstructors.getNumber() + 1)
                .size(allInstructors.getNumberOfElements())
                .instructorResponses(allInstructors.getContent())
                .build();
    }

    @Override
    @Transactional
    public SimpleResponse update(InstructorUpdateRequest instructorRequest, Long instructorId) {
        Instructor instructor = instructorRepository.findInstructorById(instructorId)
                .orElseThrow(() -> new NotFoundException("Инструктор не найден!"));
        User user = instructor.getUser();
        if (!user.getEmail().equals(instructorRequest.getEmail())) {
            boolean b = userRepository.existsByEmail(instructorRequest.getEmail());
            if (b) throw new AlreadyExistsException("Электронная почта уже существует");
        }
        String fullName = instructorRequest.getFirstName() + " " + instructorRequest.getLastName();
        user.setFullName(fullName);
        user.setEmail(instructorRequest.getEmail());
        user.setPhoneNumber(instructorRequest.getPhoneNumber());

        instructor.setSpecialization(instructorRequest.getSpecialization());
        List<Course> courses = new ArrayList<>();
        for (Long courseId : instructorRequest.getCourseIds()) {
            Course course = courseRepository.findCourseById(courseId).orElseThrow(() ->
                    new NotFoundException("Курс с идентификатором: " + courseId + " не найден"));
                courses.add(course);
        }
        instructor.setCourses(courses);
        userRepository.save(user);
        instructorRepository.save(instructor);
        return SimpleResponse.builder()
                .httpStatus(HttpStatus.OK)
                .message("Инструктор успешно обновлен")
                .build();
    }

    @Override
    @Transactional
    public SimpleResponse delete(Long instructorId) {
        User authUser = userRepository.getByEmail(SecurityContextHolder.getContext().getAuthentication().getName());
        Instructor instructor = instructorRepository.findInstructorById(instructorId)
                .orElseThrow(() -> new NotFoundException("Инструктор не найден!!!"));
        if (instructor.getTrash() == null) {
            Trash trash = new Trash();
            trash.setType(Type.INSTRUCTOR);
            trash.setName(instructor.getUser().getFullName());
            trash.setDateOfDelete(ZonedDateTime.now());
            trash.setCleanerId(authUser.getId());
            instructor.setTrash(trash);
            trashRepository.save(trash);
        }
        else throw new AlreadyExistsException("Данные уже в корзине");

        return SimpleResponse.builder()
                .httpStatus(HttpStatus.OK)
                .message("Инструктор успешно добавлен в корзину!")
                .build();
    }

    @Override
    public FindByIdInstructorResponse findById(Long instructorId) {
        Instructor instructor = instructorRepository.findInstructorById(instructorId).orElseThrow(() ->
                new NotFoundException("инструктор не найден!!!"));
        List<String> courseNames = new ArrayList<>();
        for (Course course : instructor.getCourses()) {
            courseNames.add(course.getTitle());
        }
        return FindByIdInstructorResponse.builder()
                .id(instructor.getId())
                .fullName(instructor.getUser().getFullName())
                .specialization(instructor.getSpecialization())
                .phoneNumber(instructor.getUser().getPhoneNumber())
                .email(instructor.getUser().getEmail())
                .courseNames(courseNames)
                .build();
    }

    @Override
    public List<InstructorNamesResponse> allInstructorsName() {
        return instructorRepository.AllInstructorName();
    }
}
