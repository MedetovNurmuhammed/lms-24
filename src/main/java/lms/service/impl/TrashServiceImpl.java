package lms.service.impl;

import jakarta.transaction.Transactional;
import lms.aws.service.StorageService;
import lms.dto.response.AllTrashResponse;
import lms.dto.response.SimpleResponse;
import lms.dto.response.TrashResponse;
import lms.entities.User;
import lms.entities.Trash;
import lms.entities.Course;
import lms.entities.Instructor;
import lms.entities.Group;
import lms.entities.Student;
import lms.entities.Presentation;
import lms.entities.Test;
import lms.entities.Task;
import lms.entities.Lesson;
import lms.entities.Link;
import lms.entities.Video;
import lms.entities.Notification;
import lms.entities.ResultTask;
import lms.enums.Role;
import lms.enums.Type;
import lms.exceptions.BadRequestException;
import lms.exceptions.ForbiddenException;
import lms.exceptions.NotFoundException;
import lms.repository.TrashRepository;
import lms.repository.UserRepository;
import lms.repository.TestRepository;
import lms.repository.PresentationRepository;
import lms.repository.GroupRepository;
import lms.repository.CourseRepository;
import lms.repository.InstructorRepository;
import lms.repository.StudentRepository;
import lms.repository.TaskRepository;
import lms.repository.LessonRepository;
import lms.repository.VideoRepository;
import lms.repository.LinkRepository;
import lms.service.TrashService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TrashServiceImpl implements TrashService {

    private final TrashRepository trashRepository;
    private final UserRepository userRepository;
    private final TestRepository testRepository;
    private final PresentationRepository presentationRepository;
    private final GroupRepository groupRepository;
    private final CourseRepository courseRepository;
    private final InstructorRepository instructorRepository;
    private final StudentRepository studentRepository;
    private final TaskRepository taskRepository;
    private final LessonRepository lessonRepository;
    private final VideoRepository videoRepository;
    private final LinkRepository linkRepository;
    private final StorageService storageService;

    @Override
    public AllTrashResponse findAll(int page, int size) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.getByEmail(email);
        Pageable pageable = PageRequest.of(page, size);
        Page<TrashResponse> trashes = trashRepository.findAllTrashes(pageable);
        List<TrashResponse> trashResponses = new ArrayList<>();
        for (TrashResponse trashResponse : trashes) {
            if (currentUser.getRole().equals(Role.ADMIN) &&
                    (trashResponse.getType().equals(Type.COURSE) ||
                            trashResponse.getType().equals(Type.GROUP) ||
                            trashResponse.getType().equals(Type.INSTRUCTOR) ||
                            trashResponse.getType().equals(Type.STUDENT))) {
                trashResponses.add(new TrashResponse(trashResponse.getId(), trashResponse.getType(), trashResponse.getName(), trashResponse.getDateOfDelete()));
            } else if (currentUser.getRole().equals(Role.INSTRUCTOR) &&
                    (trashResponse.getType().equals(Type.VIDEO) ||
                            trashResponse.getType().equals(Type.PRESENTATION) ||
                            trashResponse.getType().equals(Type.LINK) ||
                            trashResponse.getType().equals(Type.TEST) ||
                            trashResponse.getType().equals(Type.TASK) ||
                            trashResponse.getType().equals(Type.LESSON))) {
                trashResponses.add(new TrashResponse(trashResponse.getId(), trashResponse.getType(), trashResponse.getName(), trashResponse.getDateOfDelete()));
            }
        }

        AllTrashResponse allTrashResponse = new AllTrashResponse();
        allTrashResponse.setPage(trashes.getNumber());
        allTrashResponse.setSize(trashes.getSize());
        allTrashResponse.setTrashResponses(trashResponses);
        return allTrashResponse;
    }


    @Override
    @Transactional
    public SimpleResponse delete(Long trashId) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.getByEmail(email);
        Trash trash = trashRepository.findById(trashId).
                orElseThrow(() -> new NotFoundException(" не найден!!! "));
        if (currentUser.getRole().equals(Role.ADMIN)) {
            if (trash.getCourse() != null) {
                Course course = trash.getCourse();
                for (Instructor instructor : course.getInstructors()) {
                    instructor.getCourses().remove(course);
                }
                course.setInstructors(null);
                courseRepository.deleteById(course.getId());
            } else if (trash.getGroup() != null) {
                Group group = trash.getGroup();
                for (Course cours : group.getCourses()) {
                    cours.getGroups().remove(group);
                }
                group.setCourses(null);
                groupRepository.deleteById(group.getId());
            } else if (trash.getInstructor() != null) {
                Instructor instructor = trash.getInstructor();
                trash.getInstructor().setNotifications(null);
                instructorRepository.deleteById(instructor.getId());
            } else if (trash.getStudent() != null) {
                Student student = trash.getStudent();
                studentRepository.deleteById(student.getId());
            } else throw new BadRequestException("Вы не  можете удалить этого " + trashId);
            trashRepository.delete(trash);
            return SimpleResponse.builder()
                    .httpStatus(HttpStatus.OK)
                    .message("Удален!")
                    .build();
        }
        throw new ForbiddenException("Нет доступа!");
    }

    @Override
    public SimpleResponse deleteInstructorTrash(Long trashId) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.getByEmail(email);
        Trash trash = trashRepository.findById(trashId).
                orElseThrow(() -> new NotFoundException(" не найден!!! "));
        if (currentUser.getRole().equals(Role.INSTRUCTOR)) {
            if (trash.getPresentation() != null) {
                Presentation presentation = trash.getPresentation();
//                storageService.deleteFile(presentation.getFile());
                presentationRepository.deleteById(presentation.getId());
            } else if (trash.getTest() != null) {
                Test test = trash.getTest();
                testRepository.deleteById(test.getId());
            } else if (trash.getVideo() != null) {
                Video video = trash.getVideo();
                videoRepository.deleteById(video.getId());
            } else if (trash.getTask() != null) {
                Task task = trash.getTask();
                taskRepository.deleteById(task.getId());
            } else if (trash.getLesson() != null) {
                Lesson lesson = trash.getLesson();
                lessonRepository.deleteById(lesson.getId());
            } else if (trash.getLink() != null) {
                Link link = trash.getLink();
                linkRepository.deleteById(link.getId());
            } else throw new BadRequestException("Вы не можете удалить этого " + trashId);

            trashRepository.deleteById(trashId);
            return SimpleResponse.builder()
                    .httpStatus(HttpStatus.OK)
                    .message("Удален!")
                    .build();
        }
        throw new ForbiddenException("Нет доступа!!");
    }

    @Override
    public SimpleResponse returnFromInstructorTrashToBase(Long trashId) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.getByEmail(email);
        List<Trash> all = trashRepository.findAll();
        Trash trash = trashRepository.findById(trashId)
                .orElseThrow(() -> new NotFoundException(trashId + " не найден!"));
        if (currentUser.getRole().equals(Role.INSTRUCTOR)) {
            if (trash.getVideo() != null) {
                Video video = trash.getVideo();
                video.setTrash(null);
            } else if (trash.getPresentation() != null) {
                Presentation presentation = trash.getPresentation();
                presentation.setTrash(null);
            } else if (trash.getLink() != null) {
                Link link = trash.getLink();
                link.setTrash(null);
            } else if (trash.getTest() != null) {
                Test test = trash.getTest();
                test.setTrash(null);
            } else if (trash.getTask() != null) {
                Task task = trash.getTask();
                task.setTrash(null);
            } else if (trash.getLesson() != null) {
                Lesson lesson = trash.getLesson();
                lesson.setTrash(null);
            } else throw new BadRequestException("Вы не можете удалить этого " + trashId);
            trashRepository.deleteById(trashId);
            return SimpleResponse.builder()
                    .httpStatus(HttpStatus.OK)
                    .message(trashId + " удален!")
                    .build();
        }
        throw new ForbiddenException("Доступ запрещен!");
    }

    @Override
    @Transactional
    public SimpleResponse returnToBase(Long trashId) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.getByEmail(email);
        List<Trash> all = trashRepository.findAll();
        Trash trash = trashRepository.findById(trashId)
                .orElseThrow(() -> new NotFoundException(trashId + " не найден!"));
        if (currentUser.getRole().equals(Role.ADMIN)) {
            if (trash.getCourse() != null) {
                Course course = trash.getCourse();
                course.setTrash(null);
            } else if (trash.getGroup() != null) {
                Group group = trash.getGroup();
                group.setTrash(null);
            } else if (trash.getInstructor() != null) {
                Instructor instructor = trash.getInstructor();
                instructor.setTrash(null);
            } else if (trash.getStudent() != null) {
                Student student = trash.getStudent();
                student.setTrash(null);
            } else throw new BadRequestException("Вы не можете удалить этого " + trashId);

            trashRepository.deleteById(trashId);
            return SimpleResponse.builder()
                    .httpStatus(HttpStatus.OK)
                    .message(trashId + " удален!")
                    .build();
        }
        throw new ForbiddenException("Доступ запрещен!");
    }


    @Transactional
    @Scheduled(fixedDelay = 30000)
    public void cleanupExpiredTrash() {
        ZonedDateTime fiveMinutesAgo = ZonedDateTime.now().minusMinutes(2);
        List<Trash> expiredTrashes = trashRepository.findByDateOfDeleteBefore(ZonedDateTime.now());
        for (Trash expiredTrash : expiredTrashes) {
            if (expiredTrash.getCourse() != null) {
                Course course = expiredTrash.getCourse();
                courseRepository.deleteById(course.getId());
            } else if (expiredTrash.getInstructor() != null) {
                Instructor instructor = expiredTrash.getInstructor();
                instructorRepository.deleteById(instructor.getId());
            }else if (expiredTrash.getGroup() != null) {
                Group group = expiredTrash.getGroup();
                groupRepository.deleteById(group.getId());
            } else if (expiredTrash.getStudent() != null) {
                Student student = expiredTrash.getStudent();
                studentRepository.deleteById(student.getId());
            }else if (expiredTrash.getPresentation() != null) {
                Presentation presentation = expiredTrash.getPresentation();
//                storageService.deleteFile(presentation.getFile());
                presentationRepository.deleteById(presentation.getId());
            } else if (expiredTrash.getTest() != null) {
                Test test = expiredTrash.getTest();
                testRepository.deleteById(test.getId());
            } else if (expiredTrash.getVideo() != null) {
                Video video = expiredTrash.getVideo();
                videoRepository.deleteById(video.getId());
            } else if (expiredTrash.getTask() != null) {
                Task task = expiredTrash.getTask();
                taskRepository.deleteById(task.getId());
            } else if (expiredTrash.getLesson() != null) {
                Lesson lesson = expiredTrash.getLesson();
                lessonRepository.deleteById(lesson.getId());
            } else if (expiredTrash.getLink() != null) {
                Link link = expiredTrash.getLink();
                linkRepository.deleteById(link.getId());

            }
            if (expiredTrash.getDateOfDelete().isBefore(fiveMinutesAgo)) {
                trashRepository.delete(expiredTrash);
            }
        }
    }
}

