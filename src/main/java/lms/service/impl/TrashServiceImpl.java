package lms.service.impl;

import jakarta.transaction.Transactional;
import lms.dto.response.AllTrashResponse;
import lms.dto.response.SimpleResponse;
import lms.dto.response.TrashResponse;
import lms.entities.Trash;
import lms.entities.Course;
import lms.entities.Instructor;
import lms.entities.Group;
import lms.entities.Student;
import lms.enums.Type;
import lms.repository.TrashRepository;
import lms.service.TrashService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TrashServiceImpl implements TrashService {

    private final TrashRepository trashRepository;

    @Override
    public AllTrashResponse findAll(int page, int size) {
        if (page < 1 && size < 1) throw new java.lang.IllegalArgumentException("Индекс страницы не должен быть меньше нуля");
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("id"));
        Page<Trash> trashes = trashRepository.findAll(pageable);
        List<TrashResponse> trashResponses = new ArrayList<>();
        for (Trash trash : trashes) {
            trashResponses.add(new TrashResponse(trash.getId(), trash.getType(), trash.getName(), trash.getDateOfDelete()));
        }
        AllTrashResponse allTrashResponse = new AllTrashResponse();
        allTrashResponse.setPage(page);
        allTrashResponse.setSize(size);
        allTrashResponse.setTrashResponses(trashResponses);
        return allTrashResponse;
    }

    @Override
    public SimpleResponse delete(Long trashId) {
        Trash trash = trashRepository.findTrashById(trashId);
        if (trash == null) {
            return SimpleResponse.builder()
                    .httpStatus(HttpStatus.NOT_FOUND)
                    .message(trashId + " ID не найден!")
                    .build();
        }
        if (trash.getType().equals(Type.COURSE)) {
            Course course1 = trash.getCourse();
            for (Instructor instructor1 : course1.getInstructors()) {
                instructor1.setCourses(null);
            }
            course1.setInstructors(null);
        }
        if (trash.getType().equals(Type.GROUP)) {
            Group group1 = trash.getGroup();
            for (Course course : group1.getCourses()) {
                course.setGroups(null);
            }
            group1.setCourses(null);
        }
        if (trash.getType().equals(Type.INSTRUCTOR)) {
            for (Course course : trash.getInstructor().getCourses()) {
                course.setInstructors(null);
                trash.getInstructor().setNotificationStates(null);
                trash.getInstructor().setCourses(null);
            }
        }
        if(trash.getType().equals(Type.STUDENT)) {
            Student student = trash.getStudent();
            student.setNotificationStates(null);
            student.setGroup(null);
        }
            trashRepository.delete(trash);
        return SimpleResponse.builder()
                .httpStatus(HttpStatus.OK)
                .message("Удален!")
                .build();
}

@Override
@Transactional
public SimpleResponse returnToBase(Long trashId) {
    Trash trash = trashRepository.findTrashById(trashId);
    if (trash == null) {
        return SimpleResponse.builder()
                .httpStatus(HttpStatus.NOT_FOUND)
                .message(trashId + " ID не найден!")
                .build();
    }
    if (trash.getStudent() != null) {
        Student student = trash.getStudent();
        student.setTrash(null);
        trash.setStudent(null);
    } else if (trash.getGroup() != null) {
        Group group = trash.getGroup();
        group.setTrash(null);
        trash.setGroup(null);
    } else if (trash.getCourse() != null) {
        Course course = trash.getCourse();
        course.setTrash(null);
        trash.setCourse(null);
    } else if (trash.getInstructor() != null) {
        Instructor instructor = trash.getInstructor();
        instructor.setTrash(null);
        trash.setInstructor(null);
    }
    trashRepository.delete(trash);
    return SimpleResponse.builder()
            .httpStatus(HttpStatus.OK)
            .message(trashId + " удален!")
            .build();
}

@Transactional
//@Scheduled(fixedDelay = 60000)
public void cleanupExpiredTrash() {
    ZonedDateTime fiveMinutesAgo = ZonedDateTime.now().minusMinutes(500);
    List<Trash> expiredTrashes = trashRepository.findByDateOfDeleteBefore(ZonedDateTime.now());
    for (Trash expiredTrash : expiredTrashes) {
        if (expiredTrash.getDateOfDelete().isBefore(fiveMinutesAgo)) {
            trashRepository.delete(expiredTrash);
        }
    }
}
}

