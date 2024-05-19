package lms.service.impl;

import jakarta.transaction.TransactionScoped;
import jakarta.transaction.Transactional;
import lms.aws.service.StorageService;
import lms.dto.response.AllTrashResponse;
import lms.dto.response.SimpleResponse;
import lms.dto.response.TrashResponse;
import lms.entities.*;
import lms.enums.Role;
import lms.exceptions.BadRequestException;
import lms.exceptions.ForbiddenException;
import lms.exceptions.NotFoundException;
import lms.repository.*;
import lms.service.TaskService;
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
    private final NotificationRepository notificationRepository;

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
        }else if (currentUser.getRole().equals(Role.INSTRUCTOR)) {
            Page<TrashResponse> allInstructorTrashes = trashRepository.findAllInstructorTrashes(pageable);
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
                Course course = trash.getCourse();
                for (Lesson lesson : course.getLessons()) {
                    for (Task task : lesson.getTasks()) {
                        taskService.deleteTaskById(task.getId());
                    }
                }
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
                trash.getInstructor().setNotificationStates(null);
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
    @Transactional
    public SimpleResponse deleteInstructorTrash(Long trashId) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.getByEmail(email);
        Trash trash = trashRepository.findById(trashId).
                orElseThrow(() -> new NotFoundException(" не найден!!! "));
        if (currentUser.getRole().equals(Role.INSTRUCTOR)) {
            if (trash.getPresentation() != null) {
                Presentation presentation = trash.getPresentation();
                storageService.deleteFile(presentation.getFile());
                presentationRepository.deleteById(presentation.getId());
            } else if (trash.getTest() != null) {
                Test test = trash.getTest();
                testRepository.deleteById(test.getId());
            } else if (trash.getVideo() != null) {
                Video video = trash.getVideo();
                videoRepository.deleteById(video.getId());
            } else if (trash.getTask() != null) {
                Task task = trash.getTask();
                taskService.deleteTaskById(task.getId());
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
    @Transactional
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
                    .message(trashId + " успешно возвращен!")
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
        ZonedDateTime fiveMinutesAgo = ZonedDateTime.now().minusMinutes(5000);
        List<Trash> expiredTrashes = trashRepository.findByDateOfDeleteBefore(ZonedDateTime.now());
        for (Trash expiredTrash : expiredTrashes) {
            if (expiredTrash.getCourse() != null) {
                Course course = expiredTrash.getCourse();
                courseRepository.deleteById(course.getId());
            } else if (expiredTrash.getInstructor() != null) {
                Instructor instructor = expiredTrash.getInstructor();
                instructorRepository.deleteById(instructor.getId());
            } else if (expiredTrash.getGroup() != null) {
                Group group = expiredTrash.getGroup();
                groupRepository.deleteById(group.getId());
            } else if (expiredTrash.getStudent() != null) {
                Student student = expiredTrash.getStudent();
                studentRepository.deleteById(student.getId());
            } else if (expiredTrash.getPresentation() != null) {
                Presentation presentation = expiredTrash.getPresentation();
                storageService.deleteFile(presentation.getFile());
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

