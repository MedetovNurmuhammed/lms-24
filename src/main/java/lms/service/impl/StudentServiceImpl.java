package lms.service.impl;

import jakarta.mail.MessagingException;
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
import lms.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.ArrayList;

@Service
@RequiredArgsConstructor
@Slf4j
public class StudentServiceImpl implements StudentService {
    private final StudentRepository studentRepository;
    private final UserRepository userRepository;
    private final GroupRepository groupRepository;
    private final UserService userService;

    @Override
    @Transactional
    public SimpleResponse save(StudentRequest studentRequest) throws MessagingException {
        Group group = groupRepository.findByTitle(studentRequest.groupName());
        if (group != null) {
            User user = new User();
            Student student = new Student();
            user.setFullName(studentRequest.firstName() + " " + studentRequest.lastName());
            user.setPhoneNumber(studentRequest.phoneNumber());
            user.setEmail(studentRequest.email());
            student.setStudyFormat(studentRequest.studyFormat());
            user.setRole(Role.STUDENT);
            user.setBlock(false);
            group.getStudents().add(student);
            student.setGroup(group);
            student.setUser(user);
            studentRepository.save(student);
            userRepository.save(user);
            userService.emailSender(user.getEmail());
            log.info("Успешно " + studentRequest.email()+" сохранен!");
            return SimpleResponse.builder()
                    .httpStatus(HttpStatus.OK)
                    .message("Успешно сохранен!")
                    .build();
        }
        log.error("Группа не найден!");
        throw new BadRequestException("Группа: " + studentRequest.groupName() + " не найден!");
    }

    private AllStudentsResponse getAllStudentsResponse(List<StudentResponse> studentResponses, Page<Student> allStudUnblock) {
        for (Student student : allStudUnblock) {
            StudentResponse studentResponse = StudentResponse.builder()
                    .id(student.getId())
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
    public AllStudentsResponse findAll(int page, int size) {
        List<StudentResponse> studentResponses = new ArrayList<>();
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<Student> allStudents = studentRepository.findAll(pageable);
        return getAllStudentsResponse(studentResponses, allStudents);
    }

    @Override
    public AllStudentsResponse findAllGroupStud(int page, int size, Long groupId) {
        List<StudentResponse> studentResponses = new ArrayList<>();
        Group group = groupRepository.findById(groupId).
                orElseThrow(() -> new NotFoundException("Группа не найден!"));
        for (Student student : group.getStudents()) {
            StudentResponse studentResponse = StudentResponse.builder()
                    .id(student.getId())
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
    @Transactional
    public SimpleResponse update(Long studId, StudentRequest studentRequest) {
        Student student = studentRepository.findById(studId).
                orElseThrow(() -> new NotFoundException("Студент не найден! "));
        student.getUser().setFullName(studentRequest.firstName() + ' ' + studentRequest.lastName());
        student.getUser().setPhoneNumber(studentRequest.phoneNumber());
        student.getUser().setEmail(studentRequest.email());
        Group group = groupRepository.findByTitle(studentRequest.groupName());
        if (group != null) {
            group.getStudents().add(student);
            student.setGroup(group);
        }
        else throw new NotFoundException("Группа: " + studentRequest.groupName()+ " не найден!");
        student.getGroup().setTitle(studentRequest.groupName());
        student.setStudyFormat(studentRequest.studyFormat());
        log.info("Успешно обновлен!");
        return SimpleResponse.builder()
                .httpStatus(HttpStatus.OK)
                .message("Студент успешно обновлен!")
                .build();
    }

    @Override
    @Transactional
    public SimpleResponse delete(Long studId) {
        Student student = studentRepository.findById(studId).
                orElseThrow(() -> new NotFoundException("Студент не найден! " ));
        Group group = student.getGroup();
        student.setGroup(null);
        group.getStudents().remove(student);
        User user = student.getUser();
        userRepository.delete(user);
        studentRepository.delete(student);
        log.info("Успешно удален!");
        return SimpleResponse.builder()
                .httpStatus(HttpStatus.OK)
                .message("Успешно удален!")
                .build();
    }

    @Override
    public StudentResponse findById(Long studId) {
        Student student = studentRepository.findById(studId).
                orElseThrow(() -> new NotFoundException("Студент не найден! "));
        return StudentResponse.builder()
                .id(student.getId())
                .fullName(student.getUser().getFullName())
                .phoneNumber(student.getUser().getPhoneNumber())
                .email(student.getUser().getEmail())
                .groupName(student.getGroup().getTitle())
                .studyFormat(student.getStudyFormat())
                .build();

    }
}
