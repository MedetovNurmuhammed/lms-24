package lms.service;

import jakarta.mail.MessagingException;
import lms.dto.request.StudentRequest;
import lms.dto.response.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public interface StudentService {

    SimpleResponse save(StudentRequest studentRequest, String linkForPassword) throws MessagingException;

    AllStudentResponse findAll(String keyword, String studyFormat, Long groupId, int page, int size);

    AllStudentResponse findAllGroupStud(int page, int size, Long groupId);

    SimpleResponse update(Long studId, StudentRequest studentRequest);

    SimpleResponse delete(Long studId);

    StudentResponse findById(Long studId);

    SimpleResponse importStudentsFromExcel(Long groupId, MultipartFile file, List<String> link);

    void isValidPhoneNumber(String phoneNumber);

    void isValidEmail(String email);

    StudentIsBlockResponse isBlock(Long studId);

}
