package lms.service;

import jakarta.mail.MessagingException;
import lms.dto.request.InstructorRequest;
import lms.dto.request.InstructorUpdateRequest;
import lms.dto.response.InstructorResponse;
import lms.dto.response.PageInstructorResponses;
import lms.dto.response.SimpleResponse;

public interface InstructorService {
    SimpleResponse addInstructor(InstructorRequest instructorRequest) throws MessagingException;

    PageInstructorResponses findAll(int page, int size);

    SimpleResponse update(InstructorUpdateRequest instructorRequest, Long instructorId);

    SimpleResponse delete(Long instructorId);

    InstructorResponse findById(Long instructorId);

    PageInstructorResponses findByCourse(Long courseId, int page, int size);
}
