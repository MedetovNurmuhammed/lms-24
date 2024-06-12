package lms.service.impl;

import jakarta.transaction.Transactional;
import lms.dto.response.AllTrashResponse;
import lms.dto.response.SimpleResponse;
import lms.dto.response.TrashResponse;
import lms.entities.*;
import lms.enums.Messages;
import lms.enums.Role;
import lms.exceptions.NotFoundException;
import lms.repository.*;
import lms.service.TrashService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class TrashServiceImpl implements TrashService {
    private final NotificationRepository notificationRepository;
    private final LessonRepository lessonRepository;
    private final TaskRepository taskRepository;
    private final TestRepository testRepository;
    private final VideoRepository videoRepository;
    private final PresentationRepository presentationRepository;
    private final LinkRepository linkRepository;
    private final CourseRepository courseRepository;
    private final GroupRepository groupRepository;
    private final InstructorRepository instructorRepository;
    private final TrashRepository trashRepository;
    private final UserRepository userRepository;
    private final StudentRepository studentRepository;

    @Override
    public AllTrashResponse findAll(int page, int size) {
        User authUser = userRepository.getByEmail(SecurityContextHolder.getContext().getAuthentication().getName());
        Page<TrashResponse> trashResponses;
        if (authUser.getRole().equals(Role.ADMIN))
            trashResponses = trashRepository.findAllTrash(PageRequest.of(page - 1, size));
        else trashResponses = trashRepository.findAllTrashByAuthId(authUser.getId(), PageRequest.of(page - 1, size));
        return AllTrashResponse.builder()
                .page(trashResponses.getNumber() + 1)
                .size(trashResponses.getNumberOfElements())
                .trashResponses(trashResponses.getContent())
                .build();
    }

    @Override
    public SimpleResponse restoreData(Long trashId) {
        return SimpleResponse.builder()
                .httpStatus(HttpStatus.OK)
                .message(deleteTrash(trashRepository.findByIdOrThrow(trashId),
                        true))
                .build();
    }

    @Override
    public SimpleResponse deleteData(Long trashID) {
        return SimpleResponse.builder()
                .httpStatus(HttpStatus.OK)
                .message(deleteTrash(trashRepository.findByIdOrThrow(trashID),
                        false))
                .build();
    }



    private String deleteTrash(Trash trash, boolean isRestored) {
        return switch (trash.getType()) {
            case STUDENT -> {
                if (isRestored) {
                    studentRepository.clearStudentTrash(trash.getId());
                    trashRepository.delete(trash);
                    yield Messages.RESTORE.getMessage();
                }
                yield deleteStudent(trash);
            }
            case INSTRUCTOR -> {
                if (isRestored) {
                    instructorRepository.clearInstructorTrash(trash.getId());
                    trashRepository.delete(trash);
                    yield Messages.RESTORE.getMessage();
                }
                yield deleteInstructor(trash);

            }
            case GROUP -> {
                if (isRestored) {
                    groupRepository.clearGroupTrash(trash.getId());
                    trashRepository.delete(trash);
                    yield Messages.RESTORE.getMessage();
                }
                yield deleteGroup(trash);
            }
            case COURSE -> {
                if (isRestored) {
                    courseRepository.clearCourseTrash(trash.getId());
                    trashRepository.delete(trash);
                    yield Messages.RESTORE.getMessage();
                }
                yield deleteCourse(trash);
            }
            case LINK -> {
                if (isRestored) {
                    linkRepository.clearLinkTrash(trash.getId());
                    trashRepository.delete(trash);
                    yield Messages.RESTORE.getMessage();
                }
                yield deleteLink(trash);
            }
            case PRESENTATION -> {
                if (isRestored) {
                    presentationRepository.clearPresentationTrash(trash.getId());
                    trashRepository.delete(trash);
                    yield Messages.RESTORE.getMessage();
                }
                yield deletePresentation(trash);
            }
            case VIDEO -> {
                if (isRestored) {
                    videoRepository.clearVideoTrash(trash.getId());
                    trashRepository.delete(trash);
                    yield Messages.RESTORE.getMessage();
                }
                yield deleteVideo(trash);
            }
            case TEST -> {
                if (isRestored) {
                    testRepository.clearTestTrash(trash.getId());
                    trashRepository.delete(trash);
                    yield Messages.RESTORE.getMessage();
                }
                yield deleteTest(trash);
            }
            case TASK -> {
                if (isRestored) {
                    taskRepository.clearTaskTrash(trash.getId());
                    trashRepository.delete(trash);
                    yield Messages.RESTORE.getMessage();
                }
                yield deleteTask(trash);
            }
            case LESSON -> {
                if (isRestored) {
                    lessonRepository.clearLessonTrash(trash.getId());
                    trashRepository.delete(trash);
                    yield Messages.RESTORE.getMessage();
                }
                yield deleteLesson(trash);
            }
        };
    }

    private String deletePresentation(Trash trash) {
        Presentation presentation =
                presentationRepository.getByTrashId(trash.getId())
                        .orElseThrow(() -> new NotFoundException(Messages.NOT_FOUND.getMessage()));
        presentationRepository.delete(presentation);
        return Messages.DELETE.getMessage();
    }


    private String deleteLesson(Trash trash) { //todo: надо переделать
        Lesson lesson = lessonRepository.getLessonByTrashId(trash.getId())
                .orElseThrow(() -> new NotFoundException(Messages.NOT_FOUND.getMessage()));
        Course course = lesson.getCourse();
        course.getLessons().removeIf(l -> Objects.equals(l.getId(), lesson.getId()));
        lesson.setCourse(null);
        lessonRepository.delete(lesson);
        return Messages.DELETE.getMessage();
    }

    private String deleteTask(Trash trash) { //todo: надо переделать
        Task task = taskRepository.getTaskByTrashId(trash.getId())
                .orElseThrow(() -> new NotFoundException(Messages.NOT_FOUND.getMessage()));
        Notification notification = notificationRepository.getNotificationByTaskId(task.getId()).orElse(null);
        if (notification != null) {
            notificationRepository.clearTaskFromNotification(task.getId());
            notificationRepository.deleteNotStatInsByNotificationId(notification.getId());
            notificationRepository.deleteByNotificationId(notification.getId());
            notificationRepository.delete(notification);
        }
        taskRepository.delete(task);
        return Messages.DELETE.getMessage();
    }

    private String deleteTest(Trash trash) {
        Test test = testRepository.getTestByTrashId(trash.getId())
                .orElseThrow(() -> new NotFoundException(Messages.NOT_FOUND.getMessage()));
        testRepository.delete(test);
        return Messages.DELETE.getMessage();
    }

    private String deleteVideo(Trash trash) {
        Video video = videoRepository.getVideoByTrashId(trash.getId());
        if (video == null) throw new NotFoundException(Messages.NOT_FOUND.getMessage());
        if (video.getLesson() != null && !video.getLesson().getVideos().isEmpty()) {
            video.getLesson().getVideos()
                    .removeIf(v -> Objects.equals(v.getId(), video.getId()));
        }
        videoRepository.delete(video);
        return Messages.DELETE.getMessage();
    }

    private String deleteLink(Trash trash) {
        Link link = linkRepository.getLinkByTrashId(trash.getId())
                .orElseThrow(() -> new NotFoundException(Messages.NOT_FOUND.getMessage()));
        linkRepository.delete(link);
        return Messages.DELETE.getMessage();
    }

    private String deleteCourse(Trash trash) { //todo: надо переделать
        Course course = courseRepository.getByTrashId(trash.getId())
                .orElseThrow(() -> new NotFoundException(Messages.NOT_FOUND.getMessage()));
        courseRepository.clearInstructorsByCourseId(course.getId());
        courseRepository.clearGroupsByCourseId(course.getId());
        courseRepository.delete(course);
        return Messages.DELETE.getMessage();
    }


    public String deleteStudent(Trash trash) {
        Student student = studentRepository.getStudentByTrashId(trash.getId());
        student.getNotificationStates().keySet().forEach(notification -> {
            student.getNotificationStates().remove(notification);
            notificationRepository.delete(notification);
        });
        studentRepository.delete(student);
        return Messages.DELETE.getMessage();
    }


    public String deleteInstructor(Trash trash) {
        Instructor instructor = instructorRepository.getByTrashId(trash.getId())
                .orElseThrow(() -> new NotFoundException(Messages.NOT_FOUND.getMessage()));
        instructorRepository.delete(instructor);
        return Messages.DELETE.getMessage();
    }

    public String deleteGroup(Trash trash) {
        Group group = groupRepository.getByTrashId(trash.getId())
                .orElseThrow(() -> new NotFoundException(Messages.NOT_FOUND.getMessage()));

        groupRepository.delete(group);
        return Messages.DELETE.getMessage();
    }

}

