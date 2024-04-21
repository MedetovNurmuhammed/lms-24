package lms.repository;

import jakarta.transaction.Transactional;
import lms.entities.Instructor;
import lms.entities.Task;
import lms.entities.ResultTask;
import lms.entities.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface InstructorRepository extends JpaRepository<Instructor, Long> {

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

