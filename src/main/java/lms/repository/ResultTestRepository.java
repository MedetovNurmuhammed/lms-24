package lms.repository;

import lms.entities.ResultTest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ResultTestRepository extends JpaRepository<ResultTest, Long> {
    @Query("select s from ResultTest  s where s.student.id= :id and s.test.id = :testId")
    ResultTest findByStudentId(@Param("id") Long id, Long testId);
}
