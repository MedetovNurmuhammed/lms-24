package lms.service;

import jakarta.mail.MessagingException;
import lms.dto.request.InstructorRequest;
import lms.dto.request.InstructorUpdateRequest;
import lms.dto.response.AllInstructorResponse;
import lms.dto.response.FindByIdInstructorResponse;
import lms.dto.response.SimpleResponse;

public interface InstructorService {
    SimpleResponse addInstructor(InstructorRequest instructorRequest) throws MessagingException;

    AllInstructorResponse findAll(int page, int size);

    SimpleResponse update(InstructorUpdateRequest instructorRequest, Long instructorId);

    SimpleResponse delete(Long instructorId);

    FindByIdInstructorResponse findById(Long instructorId);

}
