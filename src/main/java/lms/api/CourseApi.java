package lms.api;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lms.dto.request.CourseRequest;
import lms.dto.response.AllInstructorsOrStudentsOfCourse;
import lms.dto.response.FindAllResponseCourse;
import lms.dto.response.SimpleResponse;
import lms.enums.Role;
import lms.service.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/course")
@CrossOrigin(origins = "*", maxAge = 3600)
public class CourseApi {
    private final CourseService courseService;

    @Secured("ADMIN")
    @PostMapping("/createCourse")
    @Operation(summary = "Создать курса",
            description = "Метод для создание курса " +
                    " Авторизация: администратор!")
    public SimpleResponse createCourse(@RequestBody @Valid CourseRequest courseRequest) {
        return courseService.createCourse(courseRequest);
    }

    @Secured("ADMIN")
    @PutMapping("/updateCourse/{courseId}")
    @Operation(summary = "Обновить курса",
            description = "Метод для обновление курса" +
                    "Авторизация: администратор!")
    public SimpleResponse editCourse(@PathVariable Long courseId,
                                     @RequestBody @Valid CourseRequest courseRequest) {
        return courseService.editCourse(courseId, courseRequest);
    }

    @Secured("ADMIN")
    @DeleteMapping("/deleteCourse/{courseId}")
    @Operation(summary = "Удалить курса по id",
            description = "Метод для удаление курса" +
                    "Авторизация: администратор!")
    public SimpleResponse deleteCourseById(@PathVariable Long courseId) {
        return courseService.deleteCourseById(courseId);
    }

    @Secured("ADMIN")
    @GetMapping("/findAllCourse")
    @Operation(summary = "Получть все курсы",
            description = "Метод для получение всех курсов" +
                    "Авторизация: администратор!")
    public FindAllResponseCourse findAllCourse(@RequestParam(required = false, defaultValue = "1") int page,
                                               @RequestParam(required = false, defaultValue = "8") int size
    ) {
        return courseService.findAllCourse(page, size);
    }

    @Secured("ADMIN")
    @PostMapping("/assignInGroupToCourse/{groupId}/{courseId}")
    @Operation(summary = "Назначить группу для прохождения курса",
            description = "Метод для назначение группу для прохождения курса" +
                    "Авторизация: администратор!")
    public SimpleResponse assignInGroupToCourse(@PathVariable Long groupId, @PathVariable Long courseId) {
        return courseService.assignInGroupToCourse(groupId, courseId);
    }

    @Secured("ADMIN")
    @PostMapping("/assignInInstructorToCourse/{courseId}/{instructorId}")
    @Operation(summary = "Добавить инструктора в группу",
            description = "Метод для добавление инструктора в группу" +
                    "Авторизация: администратор!")
    public SimpleResponse assignInstructorsToCourse(@PathVariable Long courseId, @RequestParam List<Long> instructorIds) {
        return courseService.assignInstructorsToCourse(courseId, instructorIds);
    }

    @Secured({"ADMIN","INSTRUCTOR"})
    @Operation(summary = "Возвращает пагинированный список всех инструкторов одного курса.",
            description = "(Авторизация: администратор, инструктор)")
    @GetMapping("/findAllInstructorsAndStudentsOfCourse/{courseId}")
    public AllInstructorsOrStudentsOfCourse findAllInstructorsAndStudentsOfCourse(
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "8") int size,
            @PathVariable Long courseId, @RequestParam Role role) {
        return courseService.findAllInstructorsOrStudentsByCourseId(page, size, courseId, role);
    }

    @Secured({"INSTRUCTOR", "STUDENT"})
    @GetMapping("/myCourse")
    @Operation(summary = "Получить все мои курсы",
            description = "Метод для получение моих курсов" +
                    "Авторизация: инструктор , студент!")
    public FindAllResponseCourse myCourses(@RequestParam(required = false, defaultValue = "1") int page,
                                           @RequestParam(required = false, defaultValue = "8") int size
    ) {
        return courseService.findMyCourse(page, size);
    }

}


