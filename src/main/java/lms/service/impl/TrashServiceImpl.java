package lms.service.impl;

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
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
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
        if (authUser.getRole().equals(Role.ADMIN)) {
            Page<TrashResponse> instructorTrash =
                    trashRepository.findAllTrash(PageRequest.of(page - 1, size));
            return AllTrashResponse.builder()
                    .page(instructorTrash.getNumber() + 1)
                    .size(instructorTrash.getNumberOfElements())
                    .trashResponses(instructorTrash.getContent())
                    .build();
        }
        Page<TrashResponse> instructorTrash =
                trashRepository.findAllTrashByAuthId(authUser.getId(), PageRequest.of(page - 1, size));
        return AllTrashResponse.builder()
                .page(instructorTrash.getNumber() + 1)
                .size(instructorTrash.getNumberOfElements())
                .trashResponses(instructorTrash.getContent())
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
                    yield Messages.RESTORE_TRASH.getMessage();
                }
                yield deleteStudent(trash);
            }
            case INSTRUCTOR -> {
                if (isRestored) {
                    instructorRepository.clearInstructorTrash(trash.getId());
                    trashRepository.delete(trash);
                    yield Messages.RESTORE_TRASH.getMessage();
                }
                yield deleteInstructor(trash);

            }
            case GROUP ->{
                if (isRestored) {
                    groupRepository.clearGroupTrash(trash.getId());
                    trashRepository.delete(trash);
                    yield Messages.RESTORE_TRASH.getMessage();
                }
                yield deleteGroup(trash);
            }
            case COURSE -> {
                if (isRestored) {
                    courseRepository.clearCourseTrash(trash.getId());
                    trashRepository.delete(trash);
                    yield Messages.RESTORE_TRASH.getMessage();
                }
                yield deleteCourse(trash);
            }
            case LINK -> {
                if (isRestored) {
                    linkRepository.clearLinkTrash(trash.getId());
                    trashRepository.delete(trash);
                    yield Messages.RESTORE_TRASH.getMessage();
                }
                yield deleteLink(trash);
            }
            case PRESENTATION -> {
                if (isRestored) {
                    presentationRepository.clearPresentationTrash(trash.getId());
                    trashRepository.delete(trash);
                    yield Messages.RESTORE_TRASH.getMessage();
                }
                yield deleteLink(trash);
            }
            case VIDEO -> {
                if (isRestored) {
                    videoRepository.clearVideoTrash(trash.getId());
                    trashRepository.delete(trash);
                    yield Messages.RESTORE_TRASH.getMessage();
                }
                yield deleteVideo(trash);
            }
            case TEST -> {
                if (isRestored) {
                    testRepository.clearTestTrash(trash.getId());
                    trashRepository.delete(trash);
                    yield Messages.RESTORE_TRASH.getMessage();
                }
                yield deleteTest(trash);
            }
            case TASK -> {
                if (isRestored) {
                    taskRepository.clearTaskTrash(trash.getId());
                    trashRepository.delete(trash);
                    yield Messages.RESTORE_TRASH.getMessage();
                }
                yield deleteTask(trash);
            }
            case LESSON -> {
                if (isRestored) {
                    lessonRepository.clearLessonTrash(trash.getId());
                    trashRepository.delete(trash);
                    yield Messages.RESTORE_TRASH.getMessage();
                }
                yield deleteLesson(trash);
            }
        };
    }

    private String deleteLesson(Trash trash) {
        return null;
    }

    private String deleteTask(Trash trash) {
        return null;
    }

    private String deleteTest(Trash trash) {
        return null;
    }

    private String deleteVideo(Trash trash) {
        Video video = videoRepository.getVideoByTrashId(trash.getId());
        if (video == null) throw new NotFoundException("Video with trash id: %d not found");
        if (video.getLesson() != null && !video.getLesson().getVideos().isEmpty()){
            video.getLesson().getVideos()
                    .removeIf(v -> Objects.equals(v.getId(), video.getId()));
        }
        videoRepository.delete(video);
        return Messages.DELETE_TRASH.getMessage();
    }

    private String deleteLink(Trash trash) {
        return null;
    }

    private String deleteCourse(Trash trash) {
        return null;
    }

    private String deleteStudent(Trash trash) {
        Student student = studentRepository.getStudentByTrashId(trash.getId());
        Set<Notification> notifications = student.getNotificationStates().keySet();
        for (Notification notification : notifications) {
            System.out.printf("Notification id: $%d%n", notification.getId());
        }
        notifications
                .forEach(n -> {
                    notificationRepository.deleteByNotificationId(n.getId());
                    notificationRepository.deleteById(n.getId());
                });
        studentRepository.delete(student);
        return Messages.DELETE_TRASH.getMessage();
    }

    private String deleteInstructor(Trash trash) {
        return null;
    }

    private String deleteGroup(Trash trash) {
        return null;
    }
}

