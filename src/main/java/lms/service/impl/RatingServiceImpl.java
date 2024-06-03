package lms.service.impl;

import jakarta.transaction.Transactional;
import lms.dto.response.*;
import lms.entities.*;
import lms.exceptions.NotFoundException;
import lms.repository.CourseRepository;
import lms.repository.StudentRepository;
import lms.repository.UserRepository;
import lms.service.RatingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RatingServiceImpl implements RatingService {
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final StudentRepository studentRepository;
    @Transactional
    @Override
    public RatingResponse findAllStudentRating(Long courseId) {
        List<Lesson> lessons = courseRepository.findAllLessonsByCourseId(courseId);
        List<Student> students = courseRepository.findAllStudentsByCourseId(courseId);
        List<Task> tasks = courseRepository.findAllTasksByCourseId(courseId);
        List<StudentRatingResponse> studentRatingResponses = new ArrayList<>();

        for (Student student : students) {
            Map<Long, LessonRatingResponse.LessonRatingResponseBuilder> lessonMap = new HashMap<>();
            double totalScore = 0;
            double maxPossibleScore = 0;

            for (Lesson lesson : lessons) {
                LessonRatingResponse.LessonRatingResponseBuilder lessonBuilder = LessonRatingResponse.builder()
                        .id(lesson.getId())
                        .title(lesson.getTitle())
                        .taskRatingResponses(new ArrayList<>());

                List<TaskRatingResponse> taskRatingResponses = new ArrayList<>();

                for (Task task : tasks) {
                    if (task.getLesson().equals(lesson)) {
                        // Получаем ответ студента на задание
                        AnswerTask answerTask = task.getAnswerTasks().stream()
                                .filter(a -> a.getStudent().equals(student))
                                .findFirst()
                                .orElse(null);

                        AnswerTaskRatingResponse answerTaskResponse = null;
                        if (answerTask != null) {
                            answerTaskResponse = AnswerTaskRatingResponse.builder()
                                    .id(answerTask.getId())
                                    .point(answerTask.getPoint())
                                    .build();
                            totalScore += answerTask.getPoint();
                        }
                        maxPossibleScore += 10;

                        TaskRatingResponse taskRatingResponse = TaskRatingResponse.builder()
                                .id(task.getId())
                                .taskTitle(task.getTitle())
                                .answerTaskRatingResponses(answerTaskResponse)
                                .build();

                        taskRatingResponses.add(taskRatingResponse);
                    }
                }

                lessonBuilder.taskRatingResponses(taskRatingResponses);
                lessonMap.put(lesson.getId(), lessonBuilder);
            }

            List<LessonRatingResponse> lessonResponses = lessonMap.values().stream()
                    .map(LessonRatingResponse.LessonRatingResponseBuilder::build)
                    .collect(Collectors.toList());

            double completionPercentage = (maxPossibleScore > 0) ? (totalScore / maxPossibleScore) * 100 : 0;

            StudentRatingResponse studentRatingResponse = StudentRatingResponse.builder()
                    .id(student.getId())
                    .fullName(student.getUser().getFullName())
                    .lessonRatingResponses(lessonResponses)
                    .totalScore(totalScore)
                    .completionPercentage(completionPercentage)
                    .build();

            studentRatingResponses.add(studentRatingResponse);
        }
        studentRatingResponses.sort((s1, s2) -> Double.compare(s2.getCompletionPercentage(), s1.getCompletionPercentage()));

        return RatingResponse.builder()
                .studentResponses(studentRatingResponses)
                .build();
    }

    @Override
    public StudentRatingResponse findMyRating(Principal principal, Long courseId) {
        String username = principal.getName();
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new NotFoundException("User not found"));

        Student student = studentRepository.findByUser(user)
                .orElseThrow(() -> new NotFoundException("Student not found for user: " + username));

        List<Lesson> lessons = courseRepository.findAllLessonsByCourseId(courseId);
        List<Task> tasks = courseRepository.findAllTasksByCourseId(courseId);
        double totalScore = 0;
        double maxPossibleScore = 0;

        Map<Long, LessonRatingResponse.LessonRatingResponseBuilder> lessonMap = new HashMap<>();

        for (Lesson lesson : lessons) {
            LessonRatingResponse.LessonRatingResponseBuilder lessonBuilder = LessonRatingResponse.builder()
                    .id(lesson.getId())
                    .title(lesson.getTitle())
                    .taskRatingResponses(new ArrayList<>());

            List<TaskRatingResponse> taskRatingResponses = new ArrayList<>();

            for (Task task : tasks) {
                if (task.getLesson().equals(lesson)) {
                    // Получаем ответ студента на задание
                    AnswerTask answerTask = task.getAnswerTasks().stream()
                            .filter(a -> a.getStudent().equals(student))
                            .findFirst()
                            .orElse(null);

                    AnswerTaskRatingResponse answerTaskResponse = null;
                    if (answerTask != null) {
                        answerTaskResponse = AnswerTaskRatingResponse.builder()
                                .id(answerTask.getId())
                                .point(answerTask.getPoint())
                                .build();
                        totalScore += answerTask.getPoint();
                    }
                    maxPossibleScore += 10;

                    TaskRatingResponse taskRatingResponse = TaskRatingResponse.builder()
                            .id(task.getId())
                            .taskTitle(task.getTitle())
                            .answerTaskRatingResponses(answerTaskResponse)
                            .build();

                    taskRatingResponses.add(taskRatingResponse);
                }
            }

            lessonBuilder.taskRatingResponses(taskRatingResponses);
            lessonMap.put(lesson.getId(), lessonBuilder);
        }

        List<LessonRatingResponse> lessonResponses = lessonMap.values().stream()
                .map(LessonRatingResponse.LessonRatingResponseBuilder::build)
                .collect(Collectors.toList());

        double completionPercentage = (maxPossibleScore > 0) ? (totalScore / maxPossibleScore) * 100 : 0;

        return StudentRatingResponse.builder()
                .id(student.getId())
                .fullName(student.getUser().getFullName())
                .lessonRatingResponses(lessonResponses)
                .totalScore(totalScore)
                .completionPercentage(completionPercentage)
                .build();
    }


    @Override
    public StudentTopRating findAllRatings(Long courseId) {
        List<Lesson> lessons = courseRepository.findAllLessonsByCourseId(courseId);
        List<Student> students = courseRepository.findAllStudentsByCourseId(courseId);
        List<Task> tasks = courseRepository.findAllTasksByCourseId(courseId);

        List<StudentsRatingResponse> studentRatingResponses = new ArrayList<>();

        for (Student student : students) {
            double totalScore = 0;
            double maxPossibleScore = 0;

            for (Lesson lesson : lessons) {
                for (Task task : tasks) {
                    if (task.getLesson().equals(lesson)) {
                        AnswerTask answerTask = task.getAnswerTasks().stream()
                                .filter(a -> a.getStudent().equals(student))
                                .findFirst()
                                .orElse(null);

                        if (answerTask != null) {
                            totalScore += answerTask.getPoint();
                        }
                        maxPossibleScore += 10; // Предполагаем, что каждое задание стоит 10 баллов
                    }
                }
            }

            double completionPercentage = (maxPossibleScore > 0) ? (totalScore / maxPossibleScore) * 100 : 0;

            StudentsRatingResponse studentRatingResponse = StudentsRatingResponse.builder()
                    .id(student.getId())
                    .fullName(student.getUser().getFullName())
                    .completionPercentage(completionPercentage)
                    .build();

            studentRatingResponses.add(studentRatingResponse);
        }

        List<StudentsRatingResponse> sortedStudentRatings = studentRatingResponses.stream()
                .sorted(Comparator.comparingDouble(StudentsRatingResponse::getCompletionPercentage).reversed())
                .limit(10)
                .collect(Collectors.toList());

        return StudentTopRating.builder()
                .studentsRatingResponseList(sortedStudentRatings)
                .build();
    }
    }


