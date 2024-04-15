package lms.service;

import jakarta.mail.MessagingException;
import lms.dto.request.StudentRequest;
import lms.dto.response.AllStudentsResponse;
import lms.dto.response.SimpleResponse;
import org.springframework.stereotype.Service;

@Service
public interface StudentService {

    SimpleResponse save( StudentRequest studentRequest) throws MessagingException;

    SimpleResponse createPassword(String password);

    AllStudentsResponse findAllWithoutBlock(int page,int size);

    AllStudentsResponse findAllUnBlock(int page, int size);

    AllStudentsResponse findAllBlock(int page, int size);

    AllStudentsResponse findAllGroupStud(int page, int size, Long groupId);

    AllStudentsResponse findAllStudentByStudyFormat(int page, int size, String studyFormat);

    SimpleResponse update(Long studId, StudentRequest studentRequest);

    SimpleResponse delete(Long studId);
}
