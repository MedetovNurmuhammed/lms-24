package lms.api;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lms.dto.request.ExamPointRequest;
import lms.dto.request.ExamRequest;
import lms.dto.response.ExamResponse;
import lms.dto.response.SimpleResponse;
import lms.dto.response.StudentExamResponse;
import lms.service.ExamService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/exam")
@CrossOrigin(origins = "*", maxAge = 3600)
public class ExamApi {

    private static final Logger log = LoggerFactory.getLogger(ExamApi.class);
    private final ExamService examService;
    @Secured("INSTRUCTOR")
    @PostMapping("/{courseId}")
    @Operation(summary = "Создать экзамен",description = "Метод для создание экзамена \" +\n" +
            "                    \" Авторизация: инструктор!")
    public SimpleResponse createExam(@RequestBody @Valid ExamRequest examRequest, @PathVariable Long courseId) {
        return examService.createExam(examRequest, courseId);
    }
    @Secured("INSTRUCTOR")
    @PatchMapping("/{examId}")
    @Operation(summary = "Редактивировать  экзамен",description = "метод для редактивирование  экзамена! \"+\n"+
            "\" Авторизация: Инструктор!")
    public SimpleResponse editExam(@RequestBody @Valid ExamRequest examRequest, @PathVariable Long examId) {
        return examService.editExam(examRequest, examId);
    }

    @Secured("INSTRUCTOR")
    @DeleteMapping("/{examId}")
    @Operation(summary = "Удалить экзамен",description = "метод для удаление  экзамена! \"+\n"+
            "\" Авторизация: Инструктор!")
    public SimpleResponse deleteExam(@PathVariable Long examId) {
        return examService.deleteExam(examId);
    }

    @Secured({"INSTRUCTOR","STUDENT"})
    @GetMapping("/{courseId}")
    @Operation(summary = "Получить студенты курса с экзаменами и баллами!",description = "метод для получение студентов с баллами экзамена! \"+\n"+
            "\" Авторизация: Инструктор!")
    public ResponseEntity<List<StudentExamResponse>> getStudentsAndExamsByCourseId(@Valid @PathVariable Long courseId) {
        return new ResponseEntity<>(examService.getStudentsAndExamsByCourseId(courseId), HttpStatus.OK);
    }

    @Secured("INSTRUCTOR")
    @PatchMapping("editExamPoint/{examResultId}")
    @Operation(summary = "Редактивироват балл студента!",description = "метод для редактивирование балл экзамена! \"+\n"+
            "\" Ывторизация: Инструктор!")
    public SimpleResponse editExamPoint(@RequestBody ExamPointRequest examPointRequest, @PathVariable Long examResultId) {
        return examService.editExamPoint(examPointRequest, examResultId);
    }

    @Secured("INSTRUCTOR")
    @GetMapping("/findExamForEdit/{examId}")
    public ExamResponse getById(@PathVariable Long examId){
        return examService.getById(examId);
    }
}
