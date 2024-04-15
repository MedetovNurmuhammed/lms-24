package lms.repository;

import jakarta.transaction.Transactional;
import lms.entities.Instructor;
import lms.entities.Notification;
import lms.entities.ResultTask;
import lms.entities.Token;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token, Long> {
    Optional<Token>findTokenByValue(String value);
    Token findByUserId(Long userId);
    List<Token> findByExpiration(LocalDateTime localDateTime);
    @Query("select s from Instructor s inner join User i on i.id = s.user.id where i.role = 'INSTRUCTOR'")
    Page<Instructor> findAllIns(Pageable pageable);

    @Query("select s from ResultTask s where s.instructor.id = :instructorId")
    ResultTask findResultTaskByInstructorId(@Param("instructorId") Long instructorId);



    @Transactional
    @Modifying
    @Query("delete from Task s where s.instructor.id = :instructorId")
    void deleteTasksByInstructorId(@Param("instructorId") Long instructorId);

    @Query("select s from Notification s where s.instructor.id = :instructorId")
    Notification deleteNotificationsByInstructorId(@Param("instructorId") Long instructorId);
    @Query("select s from Instructor s inner join User i on i.id = s.user.id where s.course.id = :courseId")
    Page<Instructor> findAllInstructorOfCourse(@Param("courseId")Pageable pageable, Long courseId);
}
