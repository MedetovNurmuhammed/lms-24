package lms.service;

import lms.dto.request.ExamPointRequest;
import lms.dto.request.ExamRequest;
import lms.dto.response.SimpleResponse;
import lms.dto.response.StudentExamResponse;

import java.util.List;

public interface ExamService {

    SimpleResponse createExam(ExamRequest examRequest, Long courseId);

    SimpleResponse editExam(ExamRequest examRequest, Long examId);

    SimpleResponse deleteExam(Long examId);

    List<StudentExamResponse> getStudentsAndExamsByCourseId(Long courseId);


    SimpleResponse editExamPoint(Long studentId, Long examId, ExamPointRequest examPointRequest);
}
