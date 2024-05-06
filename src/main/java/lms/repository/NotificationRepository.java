package lms.repository;

import jakarta.transaction.Transactional;
import lms.entities.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    @Transactional
    @Modifying
    @Query(value = "delete from instructor_notification_states where notification_states_key = :notificationStateKey", nativeQuery = true)
    void deleteNotificationFromExtraTableInstructor(Long notificationStateKey);

    @Transactional
    @Modifying
    @Query(value = "delete from student_notification_states where notification_states_key = :id", nativeQuery = true)
    void deleteNotificationFromExtraTableStudent(Long id);

}
