package lms.service.impl;

import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import lms.dto.request.StudentRequest;
import lms.dto.response.AllStudentsResponse;
import lms.dto.response.SimpleResponse;
import lms.dto.response.StudentResponse;
import lms.entities.*;
import lms.enums.Role;
import lms.exceptions.BadRequestException;
import lms.exceptions.NotFoundException;
import lms.repository.*;
import lms.service.StudentService;
import lms.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
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
    private final TrashRepository trashRepository;

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
            log.info("Успешно " + studentRequest.email() + " сохранен!");
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
            if (student.getTrash() == null) {
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
            if (student.getTrash() == null) {
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
        } else throw new NotFoundException("Группа: " + studentRequest.groupName() + " не найден!");
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
                orElseThrow(() -> new NotFoundException("Студент не найден! "));
        Trash trash = new Trash();
        trash.setName(student.getUser().getFullName());
        trash.setStudent(student);
        trash.setType(student.getType());
        trash.setDateOfDelete(ZonedDateTime.now());
        trash.setStudent(student);
        student.setTrash(trash);
        trashRepository.save(trash);
        log.info("Успешно удален!");
        return SimpleResponse.builder()
                .httpStatus(HttpStatus.OK)
                .message("Успешно удален!")
                .build();
    }

    @Override @Transactional
    public StudentResponse findById(Long studId) {
        Student student = studentRepository.findById(studId).
                orElseThrow(() -> new NotFoundException("Студент не найден! "));
        if (student.getTrash() == null) {
            return StudentResponse.builder()
                    .id(student.getId())
                    .fullName(student.getUser().getFullName())
                    .phoneNumber(student.getUser().getPhoneNumber())
                    .email(student.getUser().getEmail())
                    .groupName(student.getGroup().getTitle())
                    .studyFormat(student.getStudyFormat())
                    .build();
        }else throw new NotFoundException("Студент не найден!");
    }
}
