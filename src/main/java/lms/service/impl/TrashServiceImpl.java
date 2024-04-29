package lms.service.impl;

import jakarta.transaction.Transactional;
import lms.dto.response.AllTrashResponse;
import lms.dto.response.SimpleResponse;
import lms.dto.response.TrashResponse;
import lms.entities.*;
import lms.enums.Type;
import lms.exceptions.NotFoundException;
import lms.repository.AnswerTaskRepository;
import lms.repository.LinkRepository;
import lms.repository.TrashRepository;
import lms.repository.VideoRepository;
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
    private final AnswerTaskRepository answerTaskRepository;
    private final VideoRepository videoRepository;
    private final LinkRepository linkRepository;

    @Override
    public AllTrashResponse findAll(int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<Trash> trashes = trashRepository.findAll(pageable);
        List<TrashResponse> trashResponses = new ArrayList<>();
        for (Trash trash : trashes) {
            if (trash.getType().equals(Type.INSTRUCTOR) ||
                    trash.getType().equals(Type.STUDENT) ||
                    trash.getType().equals(Type.COURSE) ||
                    trash.getType().equals(Type.GROUP)) {
                trashResponses.add(new TrashResponse(trash.getId(), trash.getType(), trash.getName(), trash.getDateOfDelete()));
            }
        }
        AllTrashResponse allTrashResponse = new AllTrashResponse();
        allTrashResponse.setPage(page);
        allTrashResponse.setSize(size);
        allTrashResponse.setTrashResponses(trashResponses);
        return allTrashResponse;
    }

    @Override
    public SimpleResponse delete(Long trashId) {
        Trash trash = trashRepository.findById(trashId).orElseThrow(null);
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
            }
            for (Notification notification : trash.getInstructor().getNotifications()) {
                ResultTask resultTask = notification.getResultTask();
                resultTask.setInstructor(null);
            }
            for (Notification notification : trash.getInstructor().getNotifications()) {
                Task task = notification.getTask();
                task.setInstructor(null);
            }
            trash.getInstructor().setCourses(null);
            for (Notification notification : trash.getInstructor().getNotifications()) {
                notification.setInstructor(null);
            }
            trash.getInstructor().setNotifications(null);

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
        Trash trash = trashRepository.findById(trashId).orElseThrow(null);
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

    @Override
    public AllTrashResponse findAllInstructorTrash(int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<Trash> trashes = trashRepository.findAll(pageable);
        List<TrashResponse> trashResponses = new ArrayList<>();
        for (Trash trash : trashes) {
            if (trash.getType().equals(Type.VIDEO) ||
                    trash.getType().equals(Type.PRESENTATION) ||
                    trash.getType().equals(Type.LINK) ||
                    trash.getType().equals(Type.TEST) ||
                    trash.getType().equals(Type.TASK) ||
                    trash.getType().equals(Type.LESSON)) {
                trashResponses.add(new TrashResponse(trash.getId(), trash.getType(), trash.getName(), trash.getDateOfDelete()));
            }
        }
        AllTrashResponse allTrashResponse = new AllTrashResponse();
        allTrashResponse.setPage(page);
        allTrashResponse.setSize(trashResponses.size());
        allTrashResponse.setTrashResponses(trashResponses);
        return allTrashResponse;
    }

    @Override
    public SimpleResponse returnInstructorTrashToBase(Long trashId) {
        Trash trash = trashRepository.findById(trashId).orElseThrow(null);
        if (trash == null) {
            return SimpleResponse.builder()
                    .httpStatus(HttpStatus.NOT_FOUND)
                    .message(trashId + " ID не найден!")
                    .build();
        }
        if (trash.getVideo() != null) {
            Video video = trash.getVideo();
            video.setTrash(null);
            trash.setVideo(null);
        }
        if (trash.getPresentation() != null) {
            Presentation presentation = trash.getPresentation();
            Lesson lesson = presentation.getTrash().getLesson();
            if (lesson != null) {
                lesson.setPresentations(null);
            }
            presentation.setTrash(null);
            trash.setPresentation(null);
        }
        if (trash.getLink() != null) {
            Link link = trash.getLink();
            link.setTrash(null);
            trash.setLink(null);
        }
        if (trash.getTest() != null) {
            Test test = trash.getTest();
            test.setTrash(null);
            trash.setTest(null);
        }
        if (trash.getTask() != null) {
            Task task = trash.getTask();
            task.setTrash(null);
            trash.setTask(null);
        }
        if (trash.getLesson() != null) {
            Lesson lesson = trash.getLesson();
            lesson.setTrash(null);
            trash.setLesson(null);
        }
        trashRepository.delete(trash);
        return SimpleResponse.builder()
                .httpStatus(HttpStatus.OK)
                .message(trashId + " удален!")
                .build();
    }

    @Override
    @Transactional
    public SimpleResponse deleteInstructorTrash(Long trashId) {
        List<Trash> all = trashRepository.findAll();
        Trash trash = trashRepository.findById(trashId)
                .orElseThrow(() -> new NotFoundException("Not found : " + trashId));
        Video video = videoRepository.findById(trash.getVideo().getId()).orElseThrow(() -> new NotFoundException("fsd"));
//        Link link = linkRepository.findById(video.getLink().getId()).orElseThrow(() -> new NotFoundException("LINK"));
//        AnswerTask answerTask = link.getAnswerTask();
//        answerTask.setLink(null);
//        link.setAnswerTask(null);
        trashRepository.deleteById(trash.getId());

        return SimpleResponse.builder()
                .httpStatus(HttpStatus.OK)
                .message("Успешно удалено!")
                .build();
    }

    @Transactional
    @Scheduled(fixedDelay = 500000000)
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

