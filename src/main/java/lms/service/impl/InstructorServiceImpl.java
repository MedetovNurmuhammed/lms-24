package lms.service.impl;

import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import lms.dto.request.InstructorRequest;
import lms.dto.request.InstructorUpdateRequest;
import lms.dto.response.InstructorResponse;
import lms.dto.response.PageInstructorResponses;
import lms.dto.response.SimpleResponse;
import lms.entities.*;
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
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Service
@Validated
@RequiredArgsConstructor
public class InstructorServiceImpl implements InstructorService {
    private final InstructorRepository instructorRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JavaMailSender javaMailSender;
    private final UserService userService;
    private final CourseRepository courseRepository;

    @Override
    public SimpleResponse addInstructor(InstructorRequest instructorRequest) throws MessagingException {
       Instructor instructor = new Instructor();
       User user = new User();
       user.setFullName(instructorRequest.getFirstName()+" "+ instructorRequest.getLastName());
       instructor.setSpecialization(instructorRequest.getSpecialization());
       user.setRole(Role.INSTRUCTOR);
       user.setEmail(instructorRequest.getEmail());
       user.setBlock(false);
       user.setPhoneNumber(instructorRequest.getPhoneNumber());
       instructor.setUser(user);

            userRepository.save(user);
            userService.emailSender(user.getEmail());
            return SimpleResponse.builder()
                    .httpStatus(HttpStatus.OK)
                    .message("Saved!!!")
                    .build();

    }

    @Override
    public PageInstructorResponses findAll(int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<Instructor> usersPage = instructorRepository.findAllIns(pageable);
        List<InstructorResponse> responses = new ArrayList<>();
        for (Instructor instructor : usersPage) {
            responses.add(new InstructorResponse(instructor.getId(), instructor.getUser().getFullName(), instructor.getSpecialization(), instructor.getUser().getPhoneNumber(), instructor.getUser().getEmail(), instructor.getUser().getPassword()));
        }
        return PageInstructorResponses.builder()
                .page(usersPage.getNumber() + 1)
                .size(usersPage.getTotalPages())
                .instructorResponseList(responses)
                .build();
    }

    @Override
    @Transactional
    public SimpleResponse update(InstructorUpdateRequest instructorRequest, Long instructorId) {
        Instructor instructor = instructorRepository.findById(instructorId)
                .orElseThrow(() -> new NotFoundException("Instructor not found!!!"));
        User user = instructor.getUser();
       if(!user.getEmail().equals(instructorRequest.getEmail())){
           boolean b = userRepository.existsByEmail(instructorRequest.getEmail());
           if(b) throw new AlreadyExistsException("email already exist");
        }
        String fullName = instructorRequest.getFirstName() + " " + instructorRequest.getLastName();
        user.setFullName(fullName);
        user.setEmail(instructorRequest.getEmail());
        user.setPhoneNumber(instructorRequest.getPhoneNumber());

        instructor.setSpecialization(instructorRequest.getSpecialization());
        List<Course> courses = new ArrayList<>();
        for (String courseName : instructorRequest.getCourseNames()) {
             courses .add(courseRepository.findByName(courseName).orElseThrow(()->
                     new NotFoundException("Course title with " +courseName+ " not found")));
        }
        instructor.setCourses(courses);
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
    public InstructorResponse findById(Long instructorId) {
        Instructor instructor = instructorRepository.findById(instructorId).orElseThrow(() -> new NotFoundException("instructor not found!!!"));
        return new InstructorResponse(instructor.getId(), instructor.getUser().getFullName(), instructor.getSpecialization(), instructor.getUser().getPhoneNumber(), instructor.getUser().getEmail(), instructor.getUser().getPassword());
    }

    @Override
    public PageInstructorResponses findByCourse(Long courseId, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<Instructor> usersPage = instructorRepository.findAllInstructorOfCourse(pageable, courseId);
        List<InstructorResponse> responses = new ArrayList<>();
        for (Instructor instructor : usersPage) {
            responses.add(new InstructorResponse(instructor.getId(), instructor.getUser().getFullName(), instructor.getSpecialization(), instructor.getUser().getPhoneNumber(), instructor.getUser().getEmail(), instructor.getUser().getPassword()));
        }
        return PageInstructorResponses.builder()
                .page(usersPage.getNumber() + 1)
                .size(usersPage.getTotalPages())
                .instructorResponseList(responses)
                .build();

    }
}
