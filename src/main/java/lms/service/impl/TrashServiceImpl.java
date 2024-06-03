package lms.service.impl;

import jakarta.transaction.Transactional;
import lms.config.aws.service.StorageService;
import lms.dto.response.AllTrashResponse;
import lms.dto.response.NotificationResponse;
import lms.dto.response.SimpleResponse;
import lms.dto.response.TrashResponse;
import lms.entities.*;
import lms.repository.TrashRepository;
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
import java.util.*;

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
    private final CommentRepository commentRepository;
    private final AnnouncementRepository announcementRepository;

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
        System.err.println("WORKING...");
        ZonedDateTime fiveMinutesAgo = ZonedDateTime.now().minusMinutes(1);
        List<Trash> expiredTrashes = trashRepository.findByDateOfDeleteBefore(fiveMinutesAgo);
        for (Trash expiredTrash : expiredTrashes) {
//            if (expiredTrash.getInstructor() != null) {
//                System.err.println("WORKING1...");
//                deleteInstructor(expiredTrash.getInstructor());
////                Instructor instructor = expiredTrash.getInstructor();
////                expiredTrash.getInstructor().setNotificationStates(null);
////                User user = instructor.getUser();
////                userRepository.detachFromAnnouncement(user.getId());
////                instructorRepository.deleteById(instructor.getId());

//            } else
                if (expiredTrash.getGroup() != null) {
                System.err.println("WORKING2...");
                deleteGroup(expiredTrash);
            }
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
//            } else if (expiredTrash.getLesson() != null) {
//                Lesson lesson = expiredTrash.getLesson();
//                deleteLessonById(lesson.getId());
//
//            } else if (expiredTrash.getLink() != null) {
//                Link link = expiredTrash.getLink();
//                linkRepository.deleteById(link.getId());
//            }
//            } else if (expiredTrash.getCourse() != null) {
//                System.err.println("expiredTrash = " + expiredTrash);
//                deleteCourse(expiredTrash);
//            }
            System.err.println("expiredTrash =  asdfsdfasdfas " + expiredTrash);
//            if (expiredTrash.getDateOfDelete().isBefore(fiveMinutesAgo)) {
//                System.err.println("WORKING3...");
//                trashRepository.delete(expiredTrash);
//
//            }
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
//        group.setCourses(null);
        for (Course course : group.getCourses()) {
            System.err.println("for course delete1");
            courseRepository.deleteById(course.getId());
            System.err.println("for course delete1");
            for (Instructor instructor : course.getInstructors()) {
                System.err.println("for instructor delete1");
                instructorRepository.deleteById(instructor.getId());
                System.err.println("for instructor delete2");
            }
        }

//        groupRepository.deleteFromAdditionalTable(group.getId());
        System.err.println("delete group announcement");
        for (Student student : group.getStudents()) {
            //-----------------------
            List<Announcement> announcements = student.getUser().getAnnouncements();
            for (Announcement announcement : announcements) {
                announcementRepository.deleteById(announcement.getId());
            }
            System.err.println("user id ");
//            List<Announcement> userAnnouncements = announcementRepository.deleteByUserId(student.getUser().getId());
            student.getUser().setAnnouncements(null);
//            userRepository.detachFromAnnouncement(student.getUser().getId());
            System.err.println("after delete user announcements " + student.getId());
//            for (Announcement announcement : userAnnouncements) {
//                System.err.println("before delete1 stud");
//                announcementRepository.deleteById(announcement.getId());
//                System.err.println("before delete2 stud");
//            }
            //---------------
            System.err.println("in for notification states");
            for (AnswerTask answerTask : student.getAnswerTasks()) {
                System.err.println("in for answer task");
//                student.setNotificationStates(null);
                System.err.println("after set null notification states");
                answerTask.setStudent(null);
                System.err.println("after set null answer task states");
//                for (NotificationResponse notification1: notificationRepository.findAllNotificationResponseByAnswerTaskId(answerTask.getId())) {
                for (Notification notification : answerTask.getNotifications()) {
                    System.err.println("in for notification");
//                    student.getNotificationStates().put(null, null);//todo
                    System.err.println("null null");
//                    notificationRepository.detachNotificationFromStudents(notification.getId());//todo
                    System.err.println("after set null for notification key");
                    student.setNotificationStates(null);  //todo
                    System.err.println("after notification");
//                    notificationRepository.deleteByAnswerTaskIdAndExtraTable(student.getId());
//                    System.err.println("check " + student.getNotificationStates().keySet());
//                    notificationRepository.deleteNotificationFromExtraTableStudent(student.getId());
                    System.err.println("after delete ");

//                    notificationRepository.deleteNotificationFromExtraTableStudent(notification.getId(), student.getId());
//                    Notification notification = notificationRepository.getReferenceById(notification1.notificationId());
                    notification.setAnswerTask(null);
                    System.err.println("in for after task");
//                    removeAnswerTaskByNotification(notification.getAnswerTask());
                }
            }

            System.err.println("after deLETE STUDENT TRASH ID");
//            student.setTrash(null);
            System.err.println("TRASH ID IS " + student.getTrash().getId());
            Long trashId = student.getTrash().getId();
            Trash trash = student.getTrash();
            trash.setStudent(null);
            student.setTrash(null);
            System.err.println("l;akseflknawl;fna;eofjasljflasknflan;falkjhgfd';lkjhgfdsalkjhgfdsa");
//            trashRepository.deleteTrashById(trashId);
//            studentRepository.deleteStudentById(student.getId());
            System.err.println("ggggggggggggggghjkl");
//            trashRepository.deleteTrashById(student.getTrash().getId());

            System.err.println("student id " + student.getId() + " ann.size " + student.getAnnouncements().size());
            student.setAnnouncements(null);
//            for (Announcement announcement : student.getAnnouncements().keySet()) {
//                Student student1 = studentRepository.findByUserId(announcement.getUser().getId()).get();
//            }
//            announcementRepository.deleteByAnnouncementIdNative(student.getId());
//            student.getAnnouncements().put(null, null);

            System.err.println("DELETEEEEEEEEEEEEEEEEEEEEEEEEEEEE ");
//            userRepository.deleteStudentById(student.getUser().getId());
//            trashRepository.deleteById(student.getTrash().getId());
        }

//        for (Announcement announcement : announcementRepository.findAllByGroupId(group.getId())) {
//            System.err.println("for delete1");
//            announcementRepository.deleteById(announcement.getId());
//            System.err.println("for delete2");
//        }

        System.err.println("Before delete group");
        groupRepository.deleteById(group.getId());
        System.err.println("After delete group");


//        for (Student student : group.getStudents()) {
//            System.err.println("In for1");
//
//            // Удаляем уведомления, связанные с AnswerTask
//            for (AnswerTask answerTask : student.getAnswerTasks()) {
//                System.err.println("in for2");
//
//                // Удаляем уведомления, связанные с AnswerTask
//                List<Notification> notifications = notificationRepository.findByAnswerTaskId(answerTask.getId());
//                for (Notification notification : notifications) {
//                    System.err.println("notification id " + notification.getId());
//                    studentRepository.deleteStudentById(student.getId());
//                    System.err.println("answer task id " + answerTask.getId());
//
//                    notificationRepository.deleteByAnswerTaskId(answerTask.getId());
//                    answerTaskRepository.deleteByIdStudent(notification.getId(), student.getId());
//                }
//                System.err.println("Deleted notifications by AnswerTaskId");
//
//                // Удаляем уведомления, связанные со студентом
//                for (Notification notification : student.getNotificationStates().keySet()) {
//                    notificationRepository.deleteNotificationFromStudent(notification.getId(), student.getId());
//                    System.err.println("Deleted notification from student");
//                }
//
//                // Удаляем сам AnswerTask
//                answerTaskRepository.deleteById(answerTask.getId());
//                System.err.println("Deleted AnswerTask");
//            }
//
//            // Удаляем студента
//            studentRepository.deleteById(student.getId());
//            System.err.println("Deleted student");
//        }
    }

    private void removeAnswerTaskByNotification(AnswerTask answerTask) {
        answerTaskRepository.deleteById(answerTask.getId());
        System.err.println("deleted answer task");
    }


    //----------------------------------
//    @Transactional
//    public void deleteGroup(Trash expiredTrash) {
//        Group group = expiredTrash.getGroup();
//        group.setCourses(null);
//        System.err.println("Before delete group");
//        groupRepository.deleteById(group.getId());
//        System.err.println("After delete group");
//        for (Student student : group.getStudents()) {
//            System.err.println("In for1");
//            for (AnswerTask answerTask : student.getAnswerTasks()) {
//                System.err.println("in for2");
//                System.err.println("size " + student.getNotificationStates().keySet().size());
//
//                for (Notification notification : student.getNotificationStates().keySet()) {
//                    System.err.println("notification");
//
//                    studentRepository.deleteStudentById(student.getId());
//                    System.err.println("after delete notification");
//
//                    notificationRepository.deleteNotificationFromStudent(notification.getId(),student.getId());
//                    System.err.println("after delete student notification");
//                    answerTaskRepository.deleteByIdStudent(answerTask.getId(),student.getId());
//                    notificationRepository.delete(notification);
//                    System.err.println("TEST 1");
//
//                    notificationRepository.deleteByAnswerTaskId(answerTask.getId());
//
//
//                    System.err.println("Test 2");
//
//                }
//
//                // Удаляем уведомления, связанные с AnswerTask
//                notificationRepository.deleteNotificationsByAnswerTaskId(answerTask.getId());
//
//                System.err.println("in for2 after");
//            }
//        }
//    }

    //-------------------------------

//    public void deleteGroup(Trash expiredTrash) {
//        Group group = expiredTrash.getGroup();
//        group.setCourses(null);
////        System.out.println("get courses " + group.getCourses().size());
//        System.err.println("Before delete group");
//        groupRepository.deleteById(group.getId());
//        System.err.println("After delete group");
//        for (Student student : group.getStudents()) {
//            System.err.println("In for1");
//            for (AnswerTask answerTask : student.getAnswerTasks()) {
//                System.err.println("in for2");
//                System.err.println("size " + student.getNotificationStates().keySet().size());
//
////                notificationRepository.deleteByAnswerTaskId(answerTask.getId());
//                for (Notification notification : student.getNotificationStates().keySet()) {
//                    notification.setAnswerTask(null);
//                    System.err.println("notification");
//
////                    notificationRepository.save(notification);
////                    notificationRepository.detachNotificationFromStudents(notification.getId());
//                    student.setNotificationStates(null);
//                    notificationRepository.deleteNotificationFromStudent(notification.getId());
////                    notificationRepository.detachNotificationFromStudents(notification.getId());
////                    System.err.println("answer task size " + notification.getAnswerTask().getId());
////                    System.err.println("student notification state size " + student.getNotificationStates().keySet().size());
//                }
////                notificationRepository.deleteByAnswerTaskId(answerTask.getId());
////                notificationRepository.deleteNotificationFromExtraTableStudentMethod(answerTask.getId());
//                System.err.println("in for2 after");
//            }
//        }
//    }


//        System.err.println("before delete group");
////        groupRepository.deleteFromAdditionalTable(group.getId());
//        System.err.println("after delete group");
//        for (Student student : group.getStudents()) {
//            System.err.println("in student1 for");
//            Trash trash1 = student.getTrash();
//            System.err.println("Before delete trashStud");
//            trashRepository.deleteById(trash1.getId());
//            System.err.println("After delete trashStud");
//
//            for (AnswerTask answerTask : student.getAnswerTasks()) {
//                List<Notification> notifications = notificationRepository.findByAnswerTaskId(answerTask.getId());
//                for (Notification notification : notifications) {
//                    notificationRepository.detachNotificationFromStudents(notification.getId());
//                }
//
//                notificationRepository.deleteNotificationsByAnswerTaskId(answerTask.getId());
//                commentRepository.deleteByAnswerTaskId(answerTask.getId());
//            }
//            answerTaskRepository.deleteAnswerTaskByStudId(student.getId());
//            studentRepository.deleteById(student.getId());
//            System.err.println("After delete stud");
//        }
//        for (Course cours : group.getCourses()) {
//            System.err.println("in remove group for");
//            cours.getGroups().remove(group);
//        }
//        group.setCourses(null);
//
//        groupRepository.deleteById(group.getId());


    @Transactional
    public void deleteCourse(Trash expiredTrash) {
        Course course = expiredTrash.getCourse();
        for (Group group : course.getGroups()) {
            for (Student student : group.getStudents()) {
                for (Long notificationId : student.getNotificationStates().keySet()) {
                    Notification notification = notificationRepository.getReferenceById(notificationId);
                    notification.setAnswerTask(null);
                    courseRepository.deleteNotificationFromExtraTableStudent(notification.getId(), student.getId());
                }
            }
        }
        courseRepository.delete(course);
    }

    @Transactional
    public void deleteInstructor(Instructor instructor) {
        for (Trash trash : instructor.getTrashes()) {
            trash.setCourse(null);
        }
        instructorRepository.deleteById(instructor.getId());
    }


//    @Transactional
//    public void deleteCourse(Trash expiredTrash) {
//        Course course = expiredTrash.getCourse();
//
//        // Сначала отвязываем курс от связанных таблиц
//        courseRepository.detachFromExtraTable(course.getId());
//        courseRepository.detachFromCoursesInstructors(course.getId());
//
//        for (Lesson lesson : course.getLessons()) {

//            for (Task task : lesson.getTasks()) {
//
//                Notification notification = notificationRepository.findByTaskId(task.getId());
//                notificationRepository.deleteNotificationFromExtraTableStudent(notification.getId());
//                for (AnswerTask answerTask : task.getAnswerTasks()) {
//                    notificationRepository.detachTaskFromNotification(task.getId());
//                    notificationRepository.detachAnswerTaskFromNotification(answerTask.getId());
//
////                    notificationRepository.deleteNotificationFromExtraTableStudent(notification.getId());
//                    answerTaskRepository.deleteById(answerTask.getId());
//                }
////
//////                    task.getAnswerTasks().remove(answerTask);
//////                    List<Notification> notifications = notificationRepository.findByAnswerTaskId(answerTask.getId());
//////                    for (Notification notification : notifications) {
//////                        notificationRepository.deleteNotificationFromExtraTableStudent(notification.getId());
//////                        notificationRepository.detachAnswerTaskFromNotification(answerTask.getId());
//////                        notificationRepository.deleteNotificationFromExtraTableInstructor(notification.getId());
//////
//////                    }
////                }
//                trashRepository.deleteById(lesson.getTrash().getId());
//                lessonRepository.deleteById(lesson.getId());
//            }
}

// Удаление связей курса из дополнительных таблиц
//        courseRepository.deleteCourseGroups(course.getId());
//        courseRepository.deleteInstructorNotificationStates(course.getId());
//        courseRepository.deleteInstructorCourses(course.getId());
//
//        // Наконец, удаляем курс
//        courseRepository.delete(course);
//


//        Course course = expiredTrash.getCourse();
//        courseRepository.detachFromExtraTable(course.getId());
//        courseRepository.detachFromCoursesInstructors(course.getId());
//        for (Lesson lesson : course.getLessons()) {
//            for (Task task : lesson.getTasks()) {
//                for (AnswerTask answerTask : task.getAnswerTasks()) {
//                    List<Notification> notifications = notificationRepository.findByAnswerTaskId(answerTask.getId());
//                    for (Notification notification : notifications) {
////                        notification.setAnswerTask(null);
//                        notificationRepository.detachAnswerTaskFromNotification(answerTask.getId());
//                        notificationRepository.deleteNotificationFromExtraTableInstructor(notification.getId());
//                        notificationRepository.deleteNotificationFromExtraTableStudent(notification.getId());
//                    }
//                }
//                trashRepository.deleteById(lesson.getTrash().getId());
//                lessonRepository.deleteById(lesson.getId());
//
//            }
//        }
//        courseRepository.delete(course);
//    }


