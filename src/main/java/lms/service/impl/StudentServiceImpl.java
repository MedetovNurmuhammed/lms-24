package lms.service.impl;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.transaction.Transactional;
import lms.dto.request.StudentRequest;
import lms.dto.response.AllStudentsResponse;
import lms.dto.response.SimpleResponse;
import lms.dto.response.StudentResponse;
import lms.entities.Group;
import lms.entities.Student;
import lms.entities.User;
import lms.enums.Role;
import lms.exceptions.BadRequestException;
import lms.exceptions.NotFoundException;
import lms.repository.GroupRepository;
import lms.repository.StudentRepository;
import lms.repository.UserRepository;
import lms.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StudentServiceImpl implements StudentService {
    private final StudentRepository studentRepository;
    private final UserRepository userRepository;
    private final JavaMailSender javaMailSender;
    private final PasswordEncoder passwordEncoder;
    private final GroupRepository groupRepository;

    String email;
    int num;

    public void sendEmail(String toEmail) throws MessagingException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        email = toEmail;
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);
        mimeMessageHelper.setFrom("PEAKSOFT");
        mimeMessageHelper.setTo(toEmail);
        mimeMessageHelper.setText("""
                 <div>
                          <a href="http://localhost:8080/verify-account?email=%s" target="_blank">SET PASSWORD</a>
                        </div>
                """.formatted(email), true);
        mimeMessageHelper.setSubject("PEAKSOFT PROGRAMMING COURSES");
        javaMailSender.send(mimeMessage);
    }

    @Override
    @Transactional
    public SimpleResponse save(StudentRequest studentRequest) throws MessagingException {
        User user = new User();
        Student student = new Student();
        Group group = groupRepository.findByName(studentRequest.groupName());
        if (group != null) {
            user.setFullName(studentRequest.firstName() + " " + studentRequest.lastName());
            user.setPhoneNumber(studentRequest.phoneNumber());
            user.setEmail(studentRequest.email());
            student.setStudyFormat(studentRequest.studyFormat());
            user.setRole(Role.STUDENT);
            user.setBlock(false);
            email = user.getEmail();
            group.getStudents().add(student);
            student.setGroup(group);
            student.setUser(user);
            sendEmail(studentRequest.email());
            studentRepository.save(student);
            userRepository.save(user);
            return SimpleResponse.builder()
                    .httpStatus(HttpStatus.OK)
                    .message("Saved!!!")
                    .build();
        }
        throw new BadRequestException("Group with name " + studentRequest.groupName() + " not found!");
    }

    @Override
    @Transactional
    public SimpleResponse createPassword(String password) {
        User foundUser = userRepository.getByEmail(email);
        if (foundUser.getRole().equals(Role.STUDENT) && foundUser.getPassword() == null) {
            foundUser.setPassword(passwordEncoder.encode(password));
        } else throw new BadRequestException("User with this email not student!");
        return SimpleResponse.builder()
                .httpStatus(HttpStatus.OK)
                .message("Created password!")
                .build();
    }

    private AllStudentsResponse getAllStudentsResponse(List<StudentResponse> studentResponses, Page<Student> allStudUnblock) {
        for (Student student : allStudUnblock) {
            StudentResponse studentResponse = StudentResponse.builder()
                    .fullName(student.getUser().getFullName())
                    .phoneNumber(student.getUser().getPhoneNumber())
                    .email(student.getUser().getEmail())
                    .groupName(student.getGroup().getTitle())
                    .studyFormat(student.getStudyFormat())
                    .build();
            studentResponses.add(studentResponse);
        }
        return AllStudentsResponse.builder()
                .studentResponses(studentResponses)
                .build();
    }

    @Override
    public AllStudentsResponse findAllWithoutBlock(int page, int size) {
        List<StudentResponse> studentResponses = new ArrayList<>();
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<Student> allStudents = studentRepository.findAll(pageable);
        return getAllStudentsResponse(studentResponses, allStudents);
    }

    @Override
    public AllStudentsResponse findAllUnBlock(int page, int size) {
        List<StudentResponse> studentResponses = new ArrayList<>();
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<Student> allStudUnblock = studentRepository.findAllStudUnblock(pageable);
        return getAllStudentsResponse(studentResponses, allStudUnblock);
    }

    @Override
    public AllStudentsResponse findAllBlock(int page, int size) {
        List<StudentResponse> studentResponses = new ArrayList<>();
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<Student> allStudUnblock = studentRepository.findAllBlockStud(pageable);
        return getAllStudentsResponse(studentResponses, allStudUnblock);
    }

    @Override
    public AllStudentsResponse findAllGroupStud(int page, int size, Long groupId) {
        List<StudentResponse> studentResponses = new ArrayList<>();
        Group group = groupRepository.findById(groupId).
                orElseThrow(() -> new NotFoundException("Not found group with name " + groupId));
        for (Student student : group.getStudents()) {
            StudentResponse studentResponse = StudentResponse.builder()
                    .fullName(student.getUser().getFullName())
                    .phoneNumber(student.getUser().getPhoneNumber())
                    .email(student.getUser().getPassword())
                    .groupName(student.getGroup().getTitle())
                    .studyFormat(student.getStudyFormat())
                    .build();
            studentResponses.add(studentResponse);
        }
        return AllStudentsResponse.builder()
                .studentResponses(studentResponses)
                .build();
    }

    @Override
    public AllStudentsResponse findAllStudentByStudyFormat(int page, int size, String studyFormat) {
        List<StudentResponse> studentResponses = new ArrayList<>();
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<Student> allStudents = studentRepository.findAllStudByStudyFormat(studyFormat, pageable);
        for (Student student : allStudents) {
            StudentResponse studentResponse = StudentResponse.builder()
                    .fullName(student.getUser().getFullName())
                    .phoneNumber(student.getUser().getPhoneNumber())
                    .email(student.getUser().getPassword())
                    .groupName(student.getGroup().getTitle())
                    .studyFormat(student.getStudyFormat())
                    .build();
            studentResponses.add(studentResponse);
        }
        return AllStudentsResponse.builder()
                .studentResponses(studentResponses)
                .build();
    }

    @Override @Transactional
    public SimpleResponse update(Long studId, StudentRequest studentRequest) {
        Student student = studentRepository.findById(studId).
                orElseThrow(() -> new NotFoundException("Student not found with id " + studId));
        student.getUser().setFullName(studentRequest.firstName()+ ' ' + studentRequest.lastName());
        student.getUser().setPhoneNumber(studentRequest.phoneNumber());
        student.getUser().setEmail(studentRequest.email());
        Group group = groupRepository.findByName(studentRequest.groupName());
        if (group != null){
            group.getStudents().add(student);
            student.setGroup(group);
        }else throw new NotFoundException("Not found group with name "+ studentRequest.groupName());
        student.getGroup().setTitle(studentRequest.groupName());
        student.setStudyFormat(studentRequest.studyFormat());
        return SimpleResponse.builder()
                .httpStatus(HttpStatus.OK)
                .message("Updated!!!")
                .build();
    }

    @Override @Transactional
    public SimpleResponse delete(Long studId) {
        Student student = studentRepository.findById(studId).
                orElseThrow(() -> new NotFoundException("Not found student with id " + studId));
        Group group = student.getGroup();
        student.setGroup(null);
        group.getStudents().remove(student);
        User user = student.getUser();
        userRepository.delete(user);
        studentRepository.delete(student);
        return SimpleResponse.builder()
                .httpStatus(HttpStatus.OK)
                .message("Delete!")
                .build();
    }
}
