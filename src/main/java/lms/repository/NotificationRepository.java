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
    @Query(value = "delete from instructor_notification_states where notification_states_key = :notificationId and instructor_id = :userId", nativeQuery = true)
    void deleteNotificationFromExtraTableInstructor(@Param("notificationId")Long notificationId,@Param("userId") Long userId);

    @Transactional
    @Modifying
    @Query(value = "delete from student_notification_states where notification_states_key = :notificationId and student_id = :userId", nativeQuery = true)
    void deleteNotificationFromExtraTableStudent(@Param("notificationId")Long notificationId,@Param("userId") Long userId);

    @Transactional
    @Query(value = "select notification_states_key from student_notification_states  where notification_states_key = :notificationId and student_id = :studentId", nativeQuery = true)
    Optional<Long> findNotificationInExtraTable(@Param("studentId")Long studentId, @Param("notificationId") Long notificationId);

    @Transactional
    @Query(value = "select notification_states_key from instructor_notification_states  where notification_states_key = :notificationId and instructor_id = :instructorId", nativeQuery = true)
    Optional<Long> findNotificationInstructorInExtraTable(@Param("instructorId")Long studentId, @Param("notificationId") Long notificationId);
    @Query("select s from Notification s where s.id =:notificationId")
    Optional<Notification> findNotificationById(@Param("notificationId") Long notificationId);
}
