package lms.repository;

import lms.entities.ExamResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ExamResultRepository extends JpaRepository<ExamResult,Long> {
    @Query("SELECT er FROM ExamResult er JOIN er.exam e JOIN er.student s WHERE e.course.id = :courseId")
    List<ExamResult> findExamResultsByCourseId(@Param("courseId") Long courseId);
}
