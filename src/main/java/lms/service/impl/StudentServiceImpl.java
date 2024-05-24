package lms.service.impl;

import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.Email;
import lms.dto.request.ExcelUser;
import lms.dto.request.StudentRequest;
import lms.dto.response.AllStudentResponse;
import lms.dto.response.SimpleResponse;
import lms.dto.response.StudentResponse;
import lms.entities.Group;
import lms.entities.Student;
import lms.entities.Trash;
import lms.entities.User;
import lms.enums.Role;
import lms.enums.StudyFormat;
import lms.enums.Type;
import lms.exceptions.BadRequestException;
import lms.exceptions.NotFoundException;
import lms.exceptions.ValidationException;
import lms.repository.GroupRepository;
import lms.repository.StudentRepository;
import lms.repository.TrashRepository;
import lms.repository.UserRepository;
import lms.service.StudentService;
import lms.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.NumberToTextConverter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j

public class StudentServiceImpl implements StudentService {
    private final StudentRepository studentRepository;
    private final UserRepository userRepository;
    private final GroupRepository groupRepository;
    private final UserService userService;
    private final TrashRepository trashRepository;
    private final UserServiceImpl userServiceImpl;

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
    public AllStudentResponse findAll(String search, String studyFormat, Long groupId, int page, int size) {
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
        List<StudentResponse> studentResponses = studentRepository.findAllByGroupId(pageable, groupId);
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
        trash.setType(Type.STUDENT);
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
        } else throw new NotFoundException("Студент не найден!");
    }

    @Override
    @Validated
    public SimpleResponse importStudentsFromExcel(Long groupId, MultipartFile file) {
        Group group = groupRepository.findById(groupId).orElseThrow(() -> new NotFoundException("Группв с id: " + groupId + " не существует!"));
        try {
            Workbook workbook = WorkbookFactory.create(file.getInputStream());
            Sheet sheet = workbook.getSheetAt(0);
            List<ExcelUser> students = new ArrayList<>();
            for (Row row : sheet) {
                if (row.getPhysicalNumberOfCells() == 0) {
                    continue;
                }

                ExcelUser student = new ExcelUser();
                for (Cell cell : row) {
                    if (cell.getCellType() == CellType.BLANK) {
                        continue;
                    }

                    switch (cell.getColumnIndex()) {
                        case 0:
                            student.setFullName(getStringValue(cell));
                            break;
                        case 1:
                            student.setEmail(getStringValue(cell));
                            break;
                        case 2:
                            student.setPhoneNumber(getStringValue(cell));
                            break;
                        case 3:
                            student.setStudyFormat(StudyFormat.fromString(getStringValue(cell)));
                            break;

                        default:

                    }
                }
                students.add(student);
            }
            for (ExcelUser student : students) {
                validateStudent(student);
            }

            for (ExcelUser student : students) {
                User newUser = new User();
                Student newStudent = new Student();
                newUser.setFullName(student.getFullName());
                userServiceImpl.checkEmail(student.getEmail());
                isValidEmail(student.getEmail());
                newUser.setEmail(student.getEmail());
                newUser.setRole(Role.STUDENT);
                isValidPhoneNumber(student.getPhoneNumber());
                newUser.setPhoneNumber(student.getPhoneNumber());
                newUser.setBlock(false);
                newStudent.setStudyFormat(student.getStudyFormat());
                group.getStudents().add(newStudent);
                newStudent.setUser(newUser);
                newStudent.setGroup(group);
                User user = userRepository.save(newUser);
                studentRepository.save(newStudent);
                groupRepository.save(group);
                userServiceImpl.emailSender(user.getEmail());
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Не удалось импортировать студентов из Excel");
        } catch (MessagingException e) {
            throw new RuntimeException("Произошла ошибка при отправке или получении сообщения по почте: " + e.getMessage(), e);
        }

        return SimpleResponse.builder()
                .httpStatus(HttpStatus.OK)
                .message("Успешно импортировано!")
                .build();
    }

    @Override
    public void isValidPhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.isEmpty()) {
            throw new ValidationException("Номер телефона не может быть пустым");
        }

        if (!phoneNumber.startsWith("+996")) {
            throw new ValidationException("Номер телефона должен начинаться с +996");
        }

        if (phoneNumber.length() != 13 && !phoneNumber.substring(4).matches("^[0-9]+$")) {

            throw new ValidationException("Некорректный формат телефона!");
        }
    }

    @Override

    public void isValidEmail(@Email String email) {
        if (email == null || email.isEmpty()) {
            throw new ValidationException("Email не должен быть пустым!");
        }
        if (!email.contains("@gmail.com")) {
            throw new ValidationException("Некорректный адрес почты!");
        }
    }

    private void validateStudent(ExcelUser student) {
        isValidEmail(student.getEmail());
        isValidPhoneNumber(student.getPhoneNumber());
    }

    private String getStringValue(Cell cell) {
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                } else {
                    return NumberToTextConverter.toText(cell.getNumericCellValue());
                }
            default:
                return "";
        }
    }
}

