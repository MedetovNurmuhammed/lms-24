package lms.service.impl;

import jakarta.transaction.Transactional;
import lms.entities.Student;
import lms.repository.NotificationRepository;
import lms.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * @author Mukhammed Asantegin
 */
@Service
@RequiredArgsConstructor
public class TrashDeleteService {
    private final StudentRepository studentRepository;
    private final NotificationRepository notificationRepository;

    @Transactional
    public void deleteStudent(Student student) {
        student.getNotificationStates().clear();
        notificationRepository.clearNotificationState(student.getId());
        studentRepository.delete(student);
    }
}
