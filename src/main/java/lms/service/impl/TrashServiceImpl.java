package lms.service.impl;

import jakarta.transaction.TransactionScoped;
import jakarta.transaction.Transactional;
import lms.config.aws.service.StorageService;
import lms.dto.response.AllTrashResponse;
import lms.dto.response.SimpleResponse;
import lms.dto.response.TrashResponse;
import lms.entities.*;
import lms.enums.Role;
import lms.exceptions.BadRequestException;
import lms.exceptions.ForbiddenException;
import lms.exceptions.NotFoundException;
import lms.repository.*;
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
import java.util.List;
import java.util.Objects;

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
    private final TaskServiceImpl taskService;
    private final StorageService storageService;
    private final StudentServiceImpl studentService;
    private final NotificationRepository notificationRepository;
    private final AnswerTaskRepository answerTaskRepository;
    private final OptionRepository optionRepository;

    private Boolean isOwner(User currentUser, Trash trash) {
        Instructor instructor = instructorRepository.findByUserId(currentUser.getId())
                .orElseThrow(() -> new NotFoundException("Инструктор не найден!"));
        return trash.getInstructor().getId().equals(instructor.getId());
    }

    @Override
    public AllTrashResponse findAll(int page, int size) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.getByEmail(email);
        System.out.println("test " + currentUser);
        Pageable pageable = PageRequest.of(page - 1, size);// Пагинация начинается с 0
        if (currentUser.getRole().equals(Role.ADMIN)) {
            Page<TrashResponse> trashResponses = trashRepository.findAllTrashes(pageable);
            AllTrashResponse allTrashResponse = new AllTrashResponse();
            allTrashResponse.setPage(trashResponses.getNumber() + 1);
            allTrashResponse.setSize(trashResponses.getNumberOfElements());
            allTrashResponse.setTrashResponses(trashResponses.getContent());
            return allTrashResponse;
        } else if (currentUser.getRole().equals(Role.INSTRUCTOR)) {
            System.err.println("currentUser.getEmail() = " + currentUser.getEmail());
            Page<TrashResponse> allInstructorTrashes = trashRepository.findAllInstructorTrashes(pageable, currentUser.getId());
            System.err.println("currentUser.getEmail() = " + currentUser.getEmail());
            AllTrashResponse allTrashResponse = new AllTrashResponse();
            allTrashResponse.setPage(allInstructorTrashes.getNumber() + 1);
            allTrashResponse.setSize(allInstructorTrashes.getNumberOfElements());
            allTrashResponse.setTrashResponses(allInstructorTrashes.getContent());
            return allTrashResponse;
        } else throw new ForbiddenException("Нет доступа!!!");
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
                deleteCourse(trash);
            } else if (trash.getGroup() != null) {
                deleteGroup(trash);
            } else if (trash.getInstructor() != null) {
                Instructor instructor = trash.getInstructor();
                trash.getInstructor().setNotificationStates(null);
                User user = instructor.getUser();
                userRepository.detachFromAnnouncement(user.getId());
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

    @Transactional
    public void deleteLessonById(Long lessonId) {
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new NotFoundException("Урок не найден!"));
        for (Task task : lesson.getTasks()) {
            Notification foundTask = notificationRepository.findByTaskId(task.getId());
            foundTask.setTask(null);
        }
        lessonRepository.deleteById(lessonId);
    }

    @Transactional
    public void deleteTest(Long testId) {
        Test test = testRepository.findById(testId).
                orElseThrow(() -> new NotFoundException("Тест не найден!"));
        for (ResultTest resultTest : test.getResultTests()) {
            for (Option option : resultTest.getOptions()) {
                optionRepository.deleteOptionById(option.getId());
            }
        }
        testRepository.deleteById(testId);
    }

    @Transactional
    public void deleteVideoById(Long videoId) {
        videoRepository.deleteFromAdditionalTable(videoId);
        videoRepository.deleteById(videoId);
    }


    @Override
    @Transactional
    public SimpleResponse deleteInstructorTrash(Long trashId) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.getByEmail(email);
        Trash trash = trashRepository.findById(trashId).
                orElseThrow(() -> new NotFoundException(" не найден!!! "));
        if (isOwner(currentUser, trash)) {
            if (currentUser.getRole().equals(Role.INSTRUCTOR)) {
                if (trash.getPresentation() != null) {
                    Presentation presentation = trash.getPresentation();
                    lessonRepository.deleteFromAdditionalTable(presentation.getId());
                    storageService.deleteFile(presentation.getFile());
                    presentationRepository.deleteById(presentation.getId());
                } else if (trash.getTest() != null) {
                    Test test = trash.getTest();
                    deleteTest(test.getId());

                } else if (trash.getVideo() != null) {
                    Video video = trash.getVideo();
                    deleteVideoById(video.getId());

                } else if (trash.getTask() != null) {
                    deleteTask(trash);
                } else if (trash.getLesson() != null) {
                    Lesson lesson = trash.getLesson();
                    deleteLessonById(lesson.getId());
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
        } else throw new BadRequestException("Этот корзина не ваша!");
    }


    @Override
    @Transactional
    public SimpleResponse returnFromInstructorTrashToBase(Long trashId) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.getByEmail(email);
        List<Trash> all = trashRepository.findAll();
        Trash trash = trashRepository.findById(trashId)
                .orElseThrow(() -> new NotFoundException(trashId + " не найден!"));
        if (isOwner(currentUser, trash)) {
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
                        .message(trashId + " успешно возвращен!")
                        .build();
            }
            throw new ForbiddenException("Доступ запрещен!");

        } else throw new BadRequestException("Этот корзина не ваша!");
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
                instructor.setTrashes(null);


            } else if (trash.getStudent() != null) {
                Student student = trash.getStudent();
                student.setTrash(null);
            } else throw new BadRequestException("Вы не можете удалить этого " + trashId);

            trashRepository.deleteById(trashId);
            return SimpleResponse.builder()
                    .httpStatus(HttpStatus.OK)
                    .message(trashId + " успешно возвращен!")
                    .build();
        }
        throw new ForbiddenException("Доступ запрещен!");
    }


    @Transactional
    @Scheduled(fixedDelay = 30000)
    public void cleanupExpiredTrash() {
        ZonedDateTime fiveMinutesAgo = ZonedDateTime.now().minusMinutes(1);
        List<Trash> expiredTrashes = trashRepository.findByDateOfDeleteBefore(fiveMinutesAgo);
        for (Trash expiredTrash : expiredTrashes) {
            if (expiredTrash.getCourse() != null) {
                System.err.println("expiredTrash = " + expiredTrash);
                deleteCourse(expiredTrash);

                System.err.println("expiredTrash =  asdfsdfasdfas " + expiredTrash);
//            } else if (expiredTrash.getInstructor() != null) {
//                Instructor instructor = expiredTrash.getInstructor();
//                expiredTrash.getInstructor().setNotificationStates(null);
//                User user = instructor.getUser();
//                userRepository.detachFromAnnouncement(user.getId());
//                instructorRepository.deleteById(instructor.getId());
//            } else if (expiredTrash.getGroup() != null) {
//                deleteGroup(expiredTrash);
//            } else if (expiredTrash.getStudent() != null) {
//                Student student = expiredTrash.getStudent();
//                studentRepository.deleteById(student.getId());
//            } else if (expiredTrash.getPresentation() != null) {
//                Presentation presentation = expiredTrash.getPresentation();
//                lessonRepository.deleteFromAdditionalTable(presentation.getId());
//                storageService.deleteFile(presentation.getFile());
//                presentationRepository.deleteById(presentation.getId());
//            } else if (expiredTrash.getTest() != null) {
//                Test test = expiredTrash.getTest();
//                deleteTest(test.getId());
//            } else if (expiredTrash.getVideo() != null) {
//                Video video = expiredTrash.getVideo();
//                deleteVideoById(video.getId());
//            } else if (expiredTrash.getTask() != null) {
//                deleteTask(expiredTrash);
//
//
//
//            } else if (expiredTrash.getLesson() != null) {
//                Lesson lesson = expiredTrash.getLesson();
//                deleteLessonById(lesson.getId());
//
//            } else if (expiredTrash.getLink() != null) {
//                Link link = expiredTrash.getLink();
//                linkRepository.deleteById(link.getId());
//
//            }
                if (expiredTrash.getDateOfDelete().isBefore(fiveMinutesAgo)) {
                    trashRepository.delete(expiredTrash);

                }
            }
        }
    }

    @Transactional
    public void deleteTask(Trash expiredTrash) {
        Task task = expiredTrash.getTask();
        Notification notification = notificationRepository.findByTaskId(task.getId());
        if (notification != null) {
            notification.setTask(null);
        }
        for (AnswerTask answerTask : task.getAnswerTasks()) {
            List<Notification> notification1 = notificationRepository.findByAnswerTaskId(answerTask.getId());
            for (Notification notification2 : notification1) {

                notification2.setAnswerTask(null);
            }
        }

        taskRepository.deleteById(task.getId());
    }

    @Transactional
    public void deleteGroup(Trash expiredTrash) {
        Group group = expiredTrash.getGroup();

        groupRepository.deleteFromAdditionalTable(group.getId());
        for (Student student : group.getStudents()) {
            Trash trash1 = student.getTrash();
            trashRepository.deleteById(trash1.getId());
            studentRepository.deleteById(student.getId());
        }


        for (Course cours : group.getCourses()) {
            cours.getGroups().remove(group);
        }
        group.setCourses(null);
        for (Student student : group.getStudents()) {
            trashRepository.deleteById(student.getTrash().getId());
        }
        groupRepository.deleteById(group.getId());
    }

    @Transactional
    public void deleteCourse(Trash expiredTrash) {
        Course course = expiredTrash.getCourse();
        for (Group group : course.getGroups()) {
            group.setCourses(null);
        }
        course.setGroups(null);

        for (Lesson lesson : course.getLessons()) {
            trashRepository.deleteById(lesson.getTrash().getId());
        }
        for (Lesson lesson : course.getLessons()) {
            for (Task task : lesson.getTasks()) {
                notificationRepository.detachTaskFromNotification(task.getId());
            }
            lessonRepository.deleteById(lesson.getId());
        }
        for (Instructor instructor : course.getInstructors()) {
            instructor.getCourses().remove(course);
        }
        course.setInstructors(null);
        courseRepository.detachFromExtraTable(course.getId());

        courseRepository.deleteById(course.getId());
    }
}

