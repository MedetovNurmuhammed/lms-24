package lms.service.impl;

import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import lms.dto.request.StudentRequest;
import lms.dto.response.AllStudentResponse;
import lms.dto.response.SimpleResponse;
import lms.dto.response.StudentResponse;
import lms.entities.Group;
import lms.entities.User;
import lms.entities.Student;
import lms.entities.Trash;
import lms.enums.Role;
import lms.enums.StudyFormat;
import lms.exceptions.BadRequestException;
import lms.exceptions.NotFoundException;
import lms.repository.StudentRepository;
import lms.repository.GroupRepository;
import lms.repository.TrashRepository;
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


    @Override
    public AllStudentResponse  findAll(String search, String studyFormat, Long groupId, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        log.error(search);
        List<StudyFormat> studyFormats = new ArrayList<>();
        if (search.equalsIgnoreCase("ONLINE")) {
            search = null;
            studyFormats.add(StudyFormat.ONLINE);
        } else if (search.equalsIgnoreCase("OFFLINE")) {
            search = null;
            studyFormats.add(StudyFormat.OFFLINE);
        } else if ("".equals(studyFormat)) {
            studyFormats.addAll(List.of(StudyFormat.values()));
        } else studyFormats.add(StudyFormat.valueOf(studyFormat));

        Page<StudentResponse> allStudent = studentRepository.searchAll(search, studyFormats, groupId, pageable);

        return AllStudentResponse.builder()
                .page(allStudent.getNumber() + 1)
                .size(allStudent.getSize())
                .students(allStudent.getContent())
                .build();
    }

    @Override
    public AllStudentResponse findAllGroupStud(int page, int size, Long groupId) {
        Pageable pageable = PageRequest.of(page - 1, size);
        List<StudentResponse> studentResponses = studentRepository.findAllByGroupId(pageable,groupId);
        return AllStudentResponse.builder()
                .page(page)
                .size(size)
                .students(studentResponses)
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
        student.setTrash(trash);
        trashRepository.save(trash);
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
