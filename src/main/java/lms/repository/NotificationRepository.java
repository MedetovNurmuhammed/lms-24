package lms.repository;

import jakarta.transaction.Transactional;
import lms.entities.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    @Transactional
    @Modifying
    @Query(value = "delete from instructor_notification_states where notification_id = :notificationId and instructor_id = :userId", nativeQuery = true)
    void deleteNotificationFromExtraTableInstructor(@Param("notificationId") Long notificationId, @Param("userId") Long userId);

    @Modifying
    @Transactional
    @Query(value = "delete from student_notification_states where notification_id = :notificationId and student_id = :userId", nativeQuery = true)
    void deleteNotificationFromExtraTableStudent(@Param("notificationId") Long notificationId, @Param("userId") Long userId);

    @Query(value = "select notification_id from student_notification_states  where notification_id = :notificationId and student_id = :studentId", nativeQuery = true)
    Optional<Long> findNotificationInExtraTable(@Param("studentId") Long studentId, @Param("notificationId") Long notificationId);

    @Query(value = "select notification_id from instructor_notification_states  where notification_id = :notificationId and instructor_id = :instructorId", nativeQuery = true)
    Optional<Long> findNotificationInstructorInExtraTable(@Param("instructorId") Long studentId, @Param("notificationId") Long notificationId);

    @Query("select s from Notification s where s.id =:notificationId")
    Optional<Notification> findNotificationById(Long notificationId);

    @Modifying @Transactional
    @Query("update Notification n set n.answerTask = null , n.task = null  where n.id = :notId")
    void clearTasksFromNotificationById(Long notId);
}
