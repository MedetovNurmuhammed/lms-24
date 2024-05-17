package lms.repository.jdbcTemplateService;

import lms.dto.response.StudentTestResponse;
import lms.dto.response.TestResponseWithStudents;

import java.util.List;

public interface TestJDBCTemplate {
    List<StudentTestResponse> allStudentsWithResultTest(Long testId);
}
