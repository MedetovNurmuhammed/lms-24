package lms.service.impl;

import jakarta.transaction.Transactional;
import lms.dto.response.AllTrashResponse;
import lms.dto.response.SimpleResponse;
import lms.dto.response.TrashResponse;
import lms.entities.Course;
import lms.entities.Group;
import lms.entities.Student;
import lms.entities.Trash;
import lms.exceptions.NotFoundException;
import lms.repository.*;
import lms.service.TrashService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
    private final UserRepository userRepository;
    private final GroupRepository groupRepository;
    private final CourseRepository courseRepository;
    private final InstructorRepository instructorRepository;
    private final StudentRepository studentRepository;

    Student student = new Student();
    Course course = new Course();
    Group group = new Group();

    @Override
    public AllTrashResponse findAll(int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<Trash> trashes = trashRepository.findAll(pageable);
        List<TrashResponse> trashResponses = new ArrayList<>();
        for (Trash trash : trashes) {
            trashResponses.add(new TrashResponse(trash.getId(),trash.getType(),trash.getName(), trash.getDateOfDelete()));
        }
        AllTrashResponse allTrashResponse = new AllTrashResponse();
        allTrashResponse.setTrashResponses(trashResponses);
        return allTrashResponse;
    }

    @Override
    public SimpleResponse delete(Long trashId) {
        Trash trash = trashRepository.findTrashById(trashId);
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
        if (trash.getStudent() != null) {
            student = trash.getStudent();
            student.setTrash(null);
            trash.setStudent(null);
        } else if (trash.getGroup() != null) {
            group = trash.getGroup();
            group.setTrash(null);
            trash.setGroup(null);// Remove reference to trash
        } else if (trash.getCourse() != null) {
            course = trash.getCourse();
            course.setTrash(null);
            trash.setCourse(null);
        }

        trashRepository.delete(trash);
        return SimpleResponse.builder()
                .httpStatus(HttpStatus.OK)
                .message(trashId + " удален!")
                .build();
    }

    @Override
    public TrashResponse find(Long trashId) {
        try {
            Trash trash = trashRepository.findTrashById(trashId);
            return TrashResponse.builder()
                    .id(trash.getId())
                    .type(trash.getType())
                    .name(trash.getName())
                    .dateOfDelete(trash.getDateOfDelete())
                    .build();
        }catch (Exception e){
            throw new NotFoundException("Not found with id  " + trashId);
        }
    }

    private int count = 0;
    @Transactional
    @Scheduled(fixedDelay = 60000)
    public void cleanupExpiredTrash(){
        count++;
        ZonedDateTime fiveMinutesAgo = ZonedDateTime.now().minusMinutes(500);
        System.out.println("halfAnHourAgo = " + fiveMinutesAgo);
        List<Trash> expiredTrashes = trashRepository.findByDateOfDeleteBefore(ZonedDateTime.now());
        for (Trash expiredTrash : expiredTrashes) {
            if (expiredTrash.getDateOfDelete().isBefore(fiveMinutesAgo)){
                trashRepository.delete(expiredTrash);
            }
        }
        System.out.println(count);
        System.out.println("Size of expiredTrashes:               " + expiredTrashes.size());
    }
}

