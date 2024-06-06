package lms.service.impl;

import lms.entities.Student;
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

    public void deleteStudent(Student student) {
        studentRepository.clearNotificationState(student.getId());
        studentRepository.delete(student);
    }
}
