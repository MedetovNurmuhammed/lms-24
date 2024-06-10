package lms.service.impl;

import jakarta.transaction.Transactional;
import lms.entities.Notification;
import lms.entities.Student;
import lms.entities.Trash;
import lms.repository.NotificationRepository;
import lms.repository.StudentRepository;
import lms.repository.TrashRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @author Mukhammed Asantegin
 */
@Service
@RequiredArgsConstructor
public class TrashDeleteService {
    private final StudentRepository studentRepository;
    private final TrashRepository trashRepository;
    private final NotificationRepository notificationRepository;

    @Transactional
    public String deleteStudent(Trash trash) {
        Student student = studentRepository.getStudentByTrashId(trash.getId());
        Map<Notification, Boolean> notificationStates = student.getNotificationStates();
        if (notificationStates != null){
            for (Notification notification : notificationStates.keySet()) {
                notificationRepository.deleteByNotificationId(notification.getId());
            }
            notificationRepository.deleteAll(notificationStates.keySet());
        };
        trashRepository.delete(trash);
        studentRepository.delete(student);
        return null;
    }

    public String deleteGroup(Trash trash) {
        return null;
    }

    public String deleteInstructor(Trash trash) {
        return null;
    }
}
