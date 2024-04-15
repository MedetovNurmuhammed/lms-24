package lms.service.impl;
import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import jakarta.transaction.Transactional;
import lms.dto.request.InstructorRequest;
import lms.dto.request.InstructorUpdateRequest;
import lms.dto.response.InstructorResponse;
import lms.dto.response.PageInstructorResponses;
import lms.dto.response.SimpleResponse;
import lms.entities.*;
import lms.enums.Role;
import lms.exceptions.BadRequestException;
import lms.exceptions.NotFoundException;

import lms.repository.TokenRepository;
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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

@Service
@Validated
@RequiredArgsConstructor
public class InstructorServiceImpl implements InstructorService {
    private final InstructorRepository instructorRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JavaMailSender javaMailSender;
    private final TokenRepository tokenRepository;
     String email;
     String email2;
    @Transactional
    public SimpleResponse addInstructor(InstructorRequest instructorRequest) throws MessagingException {
        Instructor instructor = new Instructor();
        User user = new User();
        Token foundToken = tokenRepository.findByUserId(user.getId());
        user.setFullName(instructorRequest.getFirstName() + " " + instructorRequest.getLastName());
        user.setEmail(instructorRequest.getEmail());
        user.setPhoneNumber(instructorRequest.getPhoneNumber());
        user.setRole(Role.INSTRUCTOR);
        user.setBlock(false);
        userRepository.save(user);
        instructor.setSpecialization(instructorRequest.getSpecialization());
        instructor.setUser(user);
        instructorRepository.save(instructor);
        Properties properties = new Properties();
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "9090");
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("ajybeksadykov@gmail.com", "akst vqxz graa hzfe");
            }
        });

//        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
//        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
//        helper.setFrom("admin@gmail.com");
//        helper.setTo(instructorRequest.getEmail());
//        helper.setSubject("PEAKSOFT PROGRAMMING COURSES");
//        helper.setText("""
//                    <div>
//                        <a href="http://localhost:9090/lms/src/main/resources/webapp/setpassword.html?_ijt=2bf7gq5cgoohhjfnrk3l0sb7o0&_ij_reload=RELOAD_ON_SAVE" target="_blank" rel="noopener noreferrer">Set Password</a>
//                    </div>
//                """, true);
//        javaMailSender.send(mimeMessage);
        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress("ajybeksadykov@gmail.com@gmail.com"));
        message.setRecipient(Message.RecipientType.TO, InternetAddress.parse(user.getEmail()));
        message.setSubject("admin");
        String text = """
                 <div>
                                        <a href="http://localhost:9090/create-password?token=" target="_blank" rel="noopener noreferrer">Set Password</a>
                                   </div>
                """;
        if (foundToken != null && !foundToken.isExpired()){
            text +=foundToken.getValue();
        }else {
            if (foundToken !=null){
                tokenRepository.delete(foundToken);

            }
            Token token = generateToken(user, LocalDateTime.now().plusMinutes(1));
            text += token.getValue();
            tokenRepository.save(token);

        }
        message.setText(text);
        Transport.send(message);

        return SimpleResponse.builder()
                .httpStatus(HttpStatus.OK)
                .message("Check your email and set your password")
                .build();
    }

    @Transactional
    public SimpleResponse createPassword(String token, String password) {
        User user = userRepository.getByEmail(email);
        if (user != null && user.getRole() == Role.INSTRUCTOR && user.getPassword() == null) {
            user.setEmail(email);
            user.setPassword(passwordEncoder.encode(password));
            userRepository.save(user);
            return SimpleResponse.builder()
                    .httpStatus(HttpStatus.OK)
                    .message("Password created successfully!")
                    .build();
        } else {
            throw new BadRequestException("User with this email is not an instructor or already has a password!");
        }
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
    public SimpleResponse delete(Long instructorId) {
        Instructor instructor = instructorRepository.findById(instructorId).orElseThrow(() -> new NotFoundException("instructor not found!!!"));
        ResultTask resultTask = instructorRepository.findResultTaskByInstructorId(instructor.getId());
        instructorRepository.deleteTasksByInstructorId(instructor.getId());
        resultTask.setAnswerTask(null);
        resultTask.getAnswerTask().setResultTask(null);
        resultTask.getAnswerTask().getTask().setAnswerTasks(null);
        resultTask.getAnswerTask().setTask(null);

        Notification notifications = instructorRepository.deleteNotificationsByInstructorId(instructor.getId());
        for (Notification notification : notifications.getInstructor().getNotifications()) {
            notification.getInstructor().getNotifications().clear();
        }
        instructorRepository.delete(instructor);
        return SimpleResponse.builder()
                .httpStatus(HttpStatus.OK)
                .message("success deleted")
                .build();
    }

    @Override
    public InstructorResponse findById(Long instructorId) {
        Instructor instructor = instructorRepository.findById(instructorId).orElseThrow(() -> new NotFoundException("instructor not found!!!"));
        return new InstructorResponse(instructor.getId(), instructor.getUser().getFullName(),instructor.getSpecialization(), instructor.getUser().getPhoneNumber(), instructor.getUser().getEmail(),  instructor.getUser().getPassword());
    }

    @Override
    public PageInstructorResponses findByCoure(Long courseId, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<Instructor> usersPage = instructorRepository.findAllInstructorOfCourse(pageable,courseId);
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
