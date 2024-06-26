package lms.service.impl;

import jakarta.transaction.Transactional;
import lms.dto.request.ExamPointRequest;
import lms.dto.request.ExamRequest;
import lms.dto.response.ExamResponse;
import lms.dto.response.SimpleResponse;
import lms.dto.response.StudentExamResponse;
import lms.entities.Course;
import lms.entities.Exam;
import lms.entities.ExamResult;
import lms.entities.Student;
import lms.exceptions.BadRequestException;
import lms.exceptions.NotFoundException;
import lms.repository.CourseRepository;
import lms.repository.ExamRepository;
import lms.repository.ExamResultRepository;
import lms.repository.StudentRepository;
import lms.service.ExamService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
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
    @Transactional
    public SimpleResponse deleteExam(Long examId) {
        Exam exam = examRepository.findById(examId).orElseThrow(()->new NotFoundException("Экзамен с id: "+examId+" не существует!"));
        examRepository.delete(exam);
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
                //
                examInfo.setExamDate(examResult.getExam().getExamDate());
                //
                examInfo.setPoint(examResult.getPoint());
                return examInfo;
            }).collect(Collectors.toList());

            StudentExamResponse response = new StudentExamResponse();
            response.setStudentId(student.getId());
            response.setStudentName(student.getUser().getFullName());
            response.setExams(examInfos);
            return response;
        }).collect(Collectors.toList());
    }

    @Override
    public SimpleResponse editExamPoint(Long studentId, Long examId, ExamPointRequest examPointRequest) {
        Student student = studentRepository.findById(studentId).orElseThrow(() -> new NotFoundException("Студент с id: " + studentId + " не найден!"));
        Exam exam = examRepository.findById(examId).orElseThrow(() -> new NotFoundException("Экзамен с id: " + examId + " не найден!"));
        Optional<ExamResult> examResult = examResultRepository.findByStudentAndExam(student, exam);
        if (examResult.isEmpty()) {
            return new SimpleResponse(HttpStatus.NOT_FOUND, "Результат экзамена не найден!");
        }
        ExamResult examResult2 = examResult.get();
        examResult2.setPoint(examPointRequest.getPoint());
        examResultRepository.save(examResult2);

        return new SimpleResponse(HttpStatus.OK, "Балл успешно добавлен!");
    }

    @Override
    public ExamResponse getById(Long examId) {
        Exam exam = examRepository.findById(examId).orElseThrow(() -> new NotFoundException("Экзамен с id: "+examId+" не существует!"));
        return ExamResponse.builder()
                .examTitle(exam.getTitle())
                .examDate(exam.getExamDate())
                .build();
    }

}
