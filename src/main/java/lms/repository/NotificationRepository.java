package lms.repository;

import jakarta.transaction.Transactional;
import lms.entities.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    @Transactional
    @Modifying
    @Query(value = "delete from instructor_notification_states where notification_states_key = :notificationStateKey", nativeQuery = true)
    void deleteNotificationFromExtraTableInstructor(Long notificationStateKey);

    @Transactional
    @Modifying
    @Query(value = "delete from student_notification_states where notification_states_key = :id", nativeQuery = true)
    void deleteNotificationFromExtraTableStudent(Long id);

    @Query("SELECT n FROM Notification n WHERE n.task.id = :taskId")
    Notification findByTaskId(@Param("taskId") Long taskId);
    @Query("SELECT n FROM Notification n WHERE n.answerTask.id = :answerTaskId")
    List<Notification> findByAnswerTaskId(@Param("answerTaskId") Long answerTaskId);

    @Modifying
    @Query("update Notification a set a.task.id = null where a.task.id = :taskId")
    @Transactional
    void detachTaskFromNotification(Long taskId);
}
