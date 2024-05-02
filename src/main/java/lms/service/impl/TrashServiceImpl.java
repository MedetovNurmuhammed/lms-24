package lms.service.impl;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lms.dto.response.AllTrashResponse;
import lms.dto.response.SimpleResponse;
import lms.dto.response.TrashResponse;
import lms.entities.*;
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

    @Override
    public AllTrashResponse findAll(int page, int size) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.getByEmail(email);

        Pageable pageable = PageRequest.of(page - 1, size);
        Page<Trash> trashes = trashRepository.findAll(pageable);
        List<TrashResponse> trashResponses = new ArrayList<>();
        if (currentUser.getRole().equals(Role.ADMIN)) {
            for (Trash trash : trashes) {
                if (trash.getType().equals(Type.COURSE) ||
                        trash.getType().equals(Type.GROUP) ||
                        trash.getType().equals(Type.INSTRUCTOR)
                        || trash.getType().equals(Type.STUDENT)) {
                    trashResponses.add(new TrashResponse(trash.getId(), trash.getType(), trash.getName(), trash.getDateOfDelete()));
                }
            }
        } else if (currentUser.getRole().equals(Role.INSTRUCTOR)) {
            for (Trash trash : trashes) {
                if (trash.getType().equals(Type.VIDEO) ||
                        trash.getType().equals(Type.PRESENTATION) ||
                        trash.getType().equals(Type.LINK)
                        || trash.getType().equals(Type.TEST) ||
                        trash.getType().equals(Type.TASK) ||
                        trash.getType().equals(Type.LESSON)) {
                    trashResponses.add(new TrashResponse(trash.getId(), trash.getType(), trash.getName(), trash.getDateOfDelete()));
                }
            }
        }
        AllTrashResponse allTrashResponse = new AllTrashResponse();
        allTrashResponse.setPage(page);
        allTrashResponse.setSize(trashResponses.size());
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
            }else if (trash.getType().equals(Type.GROUP)) {
                Group group = trash.getGroup();
                groupRepository.deleteById(group.getId());
            }else if (trash.getInstructor() != null) {
                Instructor instructor = trash.getInstructor();
                instructorRepository.deleteById(instructor.getId());
            }else if (trash.getType().equals(Type.STUDENT)) {
                Student student = trash.getStudent();
                studentRepository.deleteById(student.getId());
            }else throw new BadRequestException("Вы не  можете удалить этого "+ trashId);
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
                presentationRepository.deleteById(presentation.getId());
            }else if (trash.getType().equals(Type.TEST)) {
                Test test = trash.getTest();
                testRepository.deleteById(test.getId());
            }else if (trash.getType().equals(Type.VIDEO)) {
                Video video = trash.getVideo();
                videoRepository.deleteById(video.getId());
            }else if (trash.getTask() != null) {
                Task task = trash.getTask();
                taskRepository.deleteById(task.getId());
            }else if (trash.getType().equals(Type.LESSON)) {
                Lesson lesson = trash.getLesson();
                lessonRepository.deleteById(lesson.getId());
            }else if (trash.getType().equals(Type.LINK)) {
                Link link = trash.getLink();
                linkRepository.deleteById(link.getId());
            }else throw new BadRequestException("Вы не можете удалить этого " + trashId);
            trashRepository.delete(trash);
            return SimpleResponse.builder()
                    .httpStatus(HttpStatus.OK)
                    .message("Удален!")
                    .build();
        }
        throw new ForbiddenException("Нет доступа!!");
    }

    @Override
    public SimpleResponse returnFromInstructorTrashToBase(Long trashId) {
        List<Trash> all = trashRepository.findAll();
        Trash trash = trashRepository.findById(trashId)
                .orElseThrow(() -> new NotFoundException(trashId + " не найден!"));
        if (trash.getVideo() != null) {
            Video video = trash.getVideo();
            video.setTrash(null);
        }else if (trash.getPresentation() != null) {
            Presentation presentation = trash.getPresentation();
            presentation.setTrash(null);
        }else if (trash.getLink() != null) {
            Link link = trash.getLink();
            link.setTrash(null);
        }else if (trash.getTest() != null) {
            Test test = trash.getTest();
            test.setTrash(null);
        }else if (trash.getTask() != null) {
            Task task = trash.getTask();
            task.setTrash(null);
        }else if (trash.getLesson() != null) {
            Lesson lesson = trash.getLesson();
            lesson.setTrash(null);
        }else throw new BadRequestException("Вы не можете удалить Id " + trashId);

        trashRepository.deleteById(trashId);
        return SimpleResponse.builder()
                .httpStatus(HttpStatus.OK)
                .message("Успешно возвращен!")
                .build();
    }

    @Override
    @Transactional
    public SimpleResponse  returnToBase(Long trashId) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.getByEmail(email);
        List<Trash> all = trashRepository.findAll();
        Trash trash = trashRepository.findById(trashId)
                .orElseThrow(() -> new NotFoundException(trashId + " не найден!"));
        if (currentUser.getRole().equals(Role.ADMIN)) {
            if (trash.getCourse() != null) {
                Course course = trash.getCourse();
                course.setTrash(null);
            }else if (trash.getGroup() != null) {
                Group group = trash.getGroup();
                group.setTrash(null);
            }else if (trash.getInstructor() != null) {
                Instructor instructor = trash.getInstructor();
                instructor.setTrash(null);
            }else if (trash.getStudent() != null) {
                Student student = trash.getStudent();
                student.setTrash(null);
            }else throw new BadRequestException("Вы не можете удалить этого "+ trashId);

            trashRepository.deleteById(trashId);
            return SimpleResponse.builder()
                    .httpStatus(HttpStatus.OK)
                    .message(trashId + " удален!")
                    .build();
        }
        throw new ForbiddenException("Доступ запрещен!");
    }


    private void detachAssociatedEntity(Trash trash) {
        if (trash.getPresentation() != null) {
            trash.getPresentation().setTrash(null);
            trash.setPresentation(null);
        } else if (trash.getLink() != null) {
            trash.getLink().setTrash(null);
            trash.setLink(null);
        } else if (trash.getTest() != null) {
            trash.getTest().setTrash(null);
            trash.setTest(null);
        } else if (trash.getTask() != null) {
            trash.getTask().setTrash(null);
            trash.setTask(null);
        } else if (trash.getLesson() != null) {
            trash.getLesson().setTrash(null);
            trash.setLesson(null);
        }
    }


    @Transactional
    @Scheduled(fixedDelay = 500000000)
    public void cleanupExpiredTrash() {
        ZonedDateTime fiveMinutesAgo = ZonedDateTime.now().minusMinutes(500);
        List<Trash> expiredTrashes = trashRepository.findByDateOfDeleteBefore(ZonedDateTime.now());
        for (Trash expiredTrash : expiredTrashes) {
            if (expiredTrash.getType().equals(Type.COURSE)) {
                for (Instructor instructor : expiredTrash.getCourse().getInstructors()) {
                    instructor.getCourses().remove(expiredTrash.getCourse());
                }
            } else if (expiredTrash.getType().equals(Type.INSTRUCTOR)) {
                for (Course course : expiredTrash.getInstructor().getCourses()) {
                    course.setInstructors(null);
                }
                for (Notification notification : expiredTrash.getInstructor().getNotifications()) {
                    ResultTask resultTask = notification.getResultTask();
                    resultTask.setInstructor(null);
                }
                for (Notification notification : expiredTrash.getInstructor().getNotifications()) {
                    Task task = notification.getTask();
                    task.setInstructor(null);
                }
                expiredTrash.getInstructor().setCourses(null);
                for (Notification notification : expiredTrash.getInstructor().getNotifications()) {
                    notification.setInstructor(null);
                }
                expiredTrash.getInstructor().setNotifications(null);
            }
            if (expiredTrash.getDateOfDelete().isBefore(fiveMinutesAgo)) {
                trashRepository.delete(expiredTrash);
            }
        }
    }
}

