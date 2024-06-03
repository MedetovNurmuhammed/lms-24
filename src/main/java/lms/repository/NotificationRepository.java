package lms.repository;

import jakarta.transaction.Transactional;
import lms.dto.response.NotificationResponse;
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

    @Modifying
    @Transactional
    @Query(value = "UPDATE student_notification_states SET notification_states_key = NULL WHERE notification_states_key = :notificationId", nativeQuery = true)
    void detachNotificationFromStudents(@Param("notificationId") Long notificationId);

    @Modifying
    @Transactional
    @Query(value = "delete from student_notification_states where notification_states_key = :notificationId", nativeQuery = true)
    void deleteNotificationFromExtraTableStudent(@Param("notificationId") Long notificationId);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM student_notification_states WHERE notification_states_key = :notificationId and student_id =:studentId", nativeQuery = true)
    void deleteNotificationFromStudent(@Param("notificationId") Long notificationId,Long studentId);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM student_notification_states WHERE notification_states_key = :notificationId", nativeQuery = true)
    void deleteNotificationFromExtraTableByNotificationId(@Param("notificationId") Long notificationId);

    @Modifying
    @Transactional
    @Query(value = "delete from student_notification_states where notification_states_key = :notificationId and student_id = :userId", nativeQuery = true)
    void deleteNotificationFromExtraTableStudent(@Param("notificationId") Long notificationId, @Param("userId") Long userId);

    @Query("SELECT n FROM Notification n WHERE n.task.id = :taskId")
    Notification findByTaskId(@Param("taskId") Long taskId);

    @Query("SELECT n FROM Notification n WHERE n.answerTask.id = :answerTaskId")
    List<Notification> findByAnswerTaskId(@Param("answerTaskId") Long answerTaskId);

    @Query("select new lms.dto.response.NotificationResponse(" +
            "n.id, " +
            "n.title, " +
            "n.description, " +
            "n.createdAt, " +
            "n.answerTask.id, " +
            "true) from Notification n where n.answerTask.id = :answerId")
    List<NotificationResponse> findAllNotificationResponseByAnswerTaskId(Long answerId);

    @Modifying
    @Transactional
    @Query("UPDATE Notification n SET n.task = null WHERE n.task.id = :taskId")
    void detachTaskFromNotification(@Param("taskId") Long taskId);

    @Modifying
    @Transactional
    @Query("delete from Notification n where n.answerTask.id =:answerTaskId")
    void deleteByAnswerTaskId(@Param("answerTaskId") Long answerTaskId);

    @Modifying
    @Transactional
    @Query(value = "delete from student_notification_states sns inner join notification n on n.id = sns.notification_states_key" +
            " inner join students s on s.id = sns.student_id" +
            " where s.id = :studentId", nativeQuery = true)
    void deleteByAnswerTaskIdAndExtraTable(Long studentId);

    @Modifying
    @Transactional
    @Query("DELETE FROM Notification n WHERE n.answerTask.id = :answerTaskId")
    void deleteNotificationsByAnswerTaskId(@Param("answerTaskId") Long answerTaskId);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM notifications WHERE id = :notificationId", nativeQuery = true)
    void deleteNotificationById(@Param("notificationId") Long notificationId);

}
