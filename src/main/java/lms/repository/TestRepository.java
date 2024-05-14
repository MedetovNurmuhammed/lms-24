package lms.repository;

import lms.dto.response.StudentResponse;
import lms.dto.response.StudentTestResponse;
import lms.entities.Test;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TestRepository extends JpaRepository<Test, Long> {

    List<StudentTestResponse> findTestsStudents(Long testId);
}
