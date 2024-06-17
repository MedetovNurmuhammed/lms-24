package lms.repository;

import lms.entities.Exam;
import lms.entities.ExamResult;
import lms.entities.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ExamResultRepository extends JpaRepository<ExamResult,Long> {
    @Query("SELECT er FROM ExamResult er JOIN er.exam e JOIN er.student s WHERE e.course.id = :courseId")
    List<ExamResult> findExamResultsByCourseId(@Param("courseId") Long courseId);
    @Query("SELECT er FROM ExamResult er WHERE er.student = :student AND er.exam = :exam")
    Optional<ExamResult> findByStudentAndExam(Student student, Exam exam);
}
