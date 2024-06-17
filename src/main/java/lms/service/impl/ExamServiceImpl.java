package lms.service.impl;

import jakarta.transaction.Transactional;
import lms.dto.request.ExamPointRequest;
import lms.dto.request.ExamRequest;
import lms.dto.response.SimpleResponse;
import lms.dto.response.StudentExamResponse;
import lms.entities.Course;
import lms.entities.Exam;
import lms.entities.ExamResult;
import lms.entities.Student;
import lms.exceptions.NotFoundException;
import lms.repository.CourseRepository;
import lms.repository.ExamRepository;
import lms.repository.ExamResultRepository;
import lms.repository.StudentRepository;
import lms.service.ExamService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExamServiceImpl implements ExamService {
    private final CourseRepository courseRepository;
    private final ExamRepository examRepository;
    private final StudentRepository studentRepository;
    private final ExamResultRepository examResultRepository;
    @Override
    @Transactional
    public SimpleResponse createExam(ExamRequest examRequest, Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new NotFoundException("Курс с id: " + courseId + " не существует!"));
        Exam exam = new Exam();
        exam.setTitle(examRequest.getTitle());
        exam.setExamDate(examRequest.getExamDate());
        List<Student> students = courseRepository.findStudentsByCourseId(courseId);
        course.getExams().add(exam);
        exam.setCourse(course);

        for (Student student : students) {
            ExamResult examResult = new ExamResult();
            examResult.setExam(exam);
            examResult.setPoint(0);
            examResult.setStudent(student);
            examRepository.save(exam);
            examResultRepository.save(examResult);
            studentRepository.save(student);
        }

        courseRepository.save(course);

        return SimpleResponse.builder()
                .httpStatus(HttpStatus.OK)
                .message("Успешно добавлено!")
                .build();
    }

    @Override
    @Transactional
    public SimpleResponse editExam(ExamRequest examRequest, Long examId) {
        Exam exam = examRepository.findById(examId).orElseThrow(()->new NotFoundException("Экзамен с id: "+examId+" не существует!"));
        exam.setTitle(examRequest.getTitle());
        exam.setExamDate(examRequest.getExamDate());
        Course course =  exam.getCourse();
        examRepository.save(exam);
        courseRepository.save(course);
        return SimpleResponse.builder()
                .httpStatus(HttpStatus.OK)
                .message("Успешно обновлено!")
                .build();
    }

    @Override
    public SimpleResponse deleteExam(Long examId) { //todo: Bugfix Nurmuhammed
        System.out.println("\"test\" = " + "test");
        Exam exam = examRepository.findById(examId).orElseThrow(()->new NotFoundException("Экзамен с id: "+examId+" не существует!"));
        examRepository.delete(exam);
        System.out.println("\"deleted\" = " + "deleted");
        return SimpleResponse.builder()
                .httpStatus(HttpStatus.OK)
                .message("Успешно удалено!")
                .build();

    }


    @Override
    public List<StudentExamResponse> getStudentsAndExamsByCourseId(Long courseId) {
        List<Student> students = courseRepository.findStudentsByCourseId(courseId);

        return students.stream().map(student -> {
            List<ExamResult> examResults = examResultRepository.findExamResultsByCourseId(courseId).stream()
                    .filter(er -> er.getStudent().getId().equals(student.getId()))
                    .toList();

            List<StudentExamResponse.ExamInfo> examInfos = examResults.stream().map(examResult -> {
                StudentExamResponse.ExamInfo examInfo = new StudentExamResponse.ExamInfo();
                examInfo.setExamId(examResult.getId());
                examInfo.setExamTitle(examResult.getExam().getTitle());
                examInfo.setPoint(examResult.getPoint());
                return examInfo;
            }).collect(Collectors.toList());

            StudentExamResponse response = new StudentExamResponse();
            response.setStudentId(student.getId());
            response.setStudentName(student.getUser().getFullName()); // Предположим, у студента есть связанный объект User с именем
            response.setExams(examInfos);
            return response;
        }).collect(Collectors.toList());
    }

    @Override
    public SimpleResponse editExamPoint(ExamPointRequest examPointRequest, Long examResultId) {
        ExamResult examResult = examResultRepository.findById(examResultId).orElseThrow(()->new NotFoundException("Результат с таким id: "+examResultId+" не существует!"));
        examResult.setPoint(examPointRequest.getPoint());
        Exam exam = examResult.getExam();
        Student student = examResult.getStudent();
        examRepository.save(exam);
        studentRepository.save(student);
        examResultRepository.save(examResult);

        return SimpleResponse.builder()
                .httpStatus(HttpStatus.OK)
                .message("Балл успешно добавлен!")
                .build();
    }
}
