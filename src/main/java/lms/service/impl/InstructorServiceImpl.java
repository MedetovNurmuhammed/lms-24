package lms.service.impl;

import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import lms.dto.request.InstructorRequest;
import lms.dto.request.InstructorUpdateRequest;
import lms.dto.response.AllInstructorResponse;
import lms.dto.response.FindByIdInstructorResponse;
import lms.dto.response.InstructorResponse;
import lms.dto.response.SimpleResponse;
import lms.entities.User;
import lms.entities.Instructor;
import lms.entities.ResultTask;
import lms.entities.Task;
import lms.entities.Notification;
import lms.entities.Course;
import lms.enums.Role;
import lms.exceptions.AlreadyExistsException;
import lms.exceptions.NotFoundException;
import lms.repository.CourseRepository;
import lms.repository.InstructorRepository;
import lms.repository.UserRepository;
import lms.service.InstructorService;
import lms.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
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

    @Override
    public SimpleResponse addInstructor(InstructorRequest instructorRequest) throws MessagingException {
        boolean exists = userRepository.existsByEmail(instructorRequest.getEmail());
        if (exists)
            throw new AlreadyExistsException("User with email " + instructorRequest.getEmail() + " already exists");

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
        userService.emailSender(user.getEmail());
        return SimpleResponse.builder()
                .httpStatus(HttpStatus.OK)
                .message("Saved!!!")
                .build();
    }



    @Override
    public AllInstructorResponse findAll(int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<InstructorResponse> allInstructors = instructorRepository.findAllInstructorsss(pageable);
        return AllInstructorResponse.builder()
                .page(allInstructors.getNumber() + 1)
                .size(allInstructors.getSize())
                .instructorResponses(allInstructors.getContent())
                .build();
    }

    @Override
    @Transactional
    public SimpleResponse update(InstructorUpdateRequest instructorRequest, Long instructorId) {
        Instructor instructor = instructorRepository.findById(instructorId)
                .orElseThrow(() -> new NotFoundException("Instructor not found!!!"));
        User user = instructor.getUser();
        if (!user.getEmail().equals(instructorRequest.getEmail())) {
            boolean b = userRepository.existsByEmail(instructorRequest.getEmail());
            if (b) throw new AlreadyExistsException("email already exist");
        }
        String fullName = instructorRequest.getFirstName() + " " + instructorRequest.getLastName();
        user.setFullName(fullName);
        user.setEmail(instructorRequest.getEmail());
        user.setPhoneNumber(instructorRequest.getPhoneNumber());

        instructor.setSpecialization(instructorRequest.getSpecialization());
        List<Course> courses = new ArrayList<>();
        for (Long courseId : instructorRequest.getCourseIds()) {
            Course course = courseRepository.findById(courseId).orElseThrow(() ->
                    new NotFoundException("Course  with id: " + courseId + " not found"));
            if (!instructor.getCourses().contains(course)) {
                courses.add(course);
            }
        }
        instructor.getCourses().addAll(courses);
        userRepository.save(user);
        instructorRepository.save(instructor);
        return SimpleResponse.builder()
                .httpStatus(HttpStatus.OK)
                .message("Successfully updated")
                .build();
    }

    @Override
    @Transactional
    public SimpleResponse delete(Long instructorId) {
        Instructor instructor = instructorRepository.findById(instructorId)
                .orElseThrow(() -> new NotFoundException("Instructor not found!!!"));
        Notification notifications = instructorRepository.findNotificationByInstructorId(instructor.getId());
        notifications.setInstructor(null);
        instructor.setNotifications(null);
        ResultTask resultTasks = instructorRepository.findResultTaskByInstructorId(instructor.getId());
        resultTasks.setInstructor(null);
        Task task = instructorRepository.findTaskByInstructorId(instructor.getId());
        task.setInstructor(null);
        User user = userRepository.findById(instructor.getUser().getId())
                .orElseThrow(() -> new NotFoundException("User not found!!!"));
        userRepository.delete(user);
        instructorRepository.delete(instructor);
        return SimpleResponse.builder()
                .httpStatus(HttpStatus.OK)
                .message("Instructor successfully deleted")
                .build();
    }

    @Override
    public FindByIdInstructorResponse findById(Long instructorId) {
        Instructor instructor = instructorRepository.findById(instructorId).orElseThrow(() -> new NotFoundException("instructor not found!!!"));
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
}
