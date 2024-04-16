package lms.service;

import jakarta.mail.MessagingException;
import lms.dto.request.StudentRequest;
import lms.dto.response.AllStudentsResponse;
import lms.dto.response.SimpleResponse;
import lms.dto.response.StudentResponse;
import lms.exceptions.BadRequestException;
import org.springframework.stereotype.Service;

@Service
public interface StudentService {

    SimpleResponse save( StudentRequest studentRequest) throws MessagingException;

    AllStudentsResponse findAll(int page, int size);

    AllStudentsResponse findAllGroupStud(int page, int size, Long groupId);

    SimpleResponse update(Long studId, StudentRequest studentRequest);

    SimpleResponse delete(Long studId);

    StudentResponse findById(Long studId);
}
