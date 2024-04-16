package lms.service.impl;

import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import lms.dto.request.InstructorRequest;
import lms.dto.request.InstructorUpdateRequest;
import lms.dto.response.InstructorResponse;
import lms.dto.response.PageInstructorResponses;
import lms.dto.response.SimpleResponse;
import lms.entities.Instructor;
import lms.entities.ResultTask;
import lms.entities.Task;
import lms.entities.User;
import lms.entities.Notification;
import lms.exceptions.NotFoundException;
import lms.repository.InstructorRepository;
import lms.repository.UserRepository;
import lms.service.InstructorService;
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
import java.util.List;

@Service
@Validated
@RequiredArgsConstructor
public class InstructorServiceImpl implements InstructorService {
    private final InstructorRepository instructorRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JavaMailSender javaMailSender;


    @Override
    public SimpleResponse addInstructor(InstructorRequest instructorRequest) throws MessagingException {
        return null;
    }

    @Override
    public SimpleResponse createPassword(String token, String password) {
        return null;
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
        String fullName = instructorRequest.getFirstName() + " " + instructorRequest.getLastName();
        user.setFullName(fullName);
        user.setPhoneNumber(instructorRequest.getPhoneNumber());
        userRepository.save(user);
        instructor.setSpecialization(instructorRequest.getSpecialization());
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
    public PageInstructorResponses findByCoure(Long courseId, int page, int size) {
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
