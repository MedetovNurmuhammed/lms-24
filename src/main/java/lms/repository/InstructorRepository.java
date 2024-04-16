package lms.repository;

import jakarta.transaction.Transactional;
import lms.entities.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface InstructorRepository extends JpaRepository<Instructor, Long> {
    @Query("select s from Instructor s inner join User i on i.id = s.user.id where i.role = 'INSTRUCTOR'")
    Page<Instructor> findAllIns(Pageable pageable);
    @Query("select s from Instructor s inner join User i on i.id = s.user.id where s.course.id = :courseId")
    Page<Instructor> findAllInstructorOfCourse(@Param("courseId")Pageable pageable, Long courseId);

    @Transactional
    @Query("select n from Notification n where n.instructor.id = :instructorId")
    Notification findNotificationByInstructorId(Long instructorId);

    @Transactional
    @Query("select rt from ResultTask rt where rt.instructor.id = :instructorId")
    ResultTask findResultTaskByInstructorId(Long instructorId);

    @Transactional
    @Query("select t from Task t where t.instructor.id = :instructorId")
    Task findTaskByInstructorId(Long instructorId);

}
