package lms.api;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lms.dto.FindAllStudentsRequestParams;
import lms.dto.request.StudentRequest;
import lms.dto.response.AllStudentResponse;
import lms.dto.response.SimpleResponse;
import lms.dto.response.StudentResponse;
import lms.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;

@RestController
@RequestMapping("/api/students")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
public class StudentApi {
    private final StudentService studentService;

    @Secured("ADMIN")
    @PostMapping("/save")
    @Operation(summary = "Сохранить студента",
            description = "Метод для сохранение студента и отправка сообщение почту чтобы создать студент создал себе пароль! " +
                          " Авторизация: администратор!")
    public SimpleResponse saveStudent(@RequestBody @Valid StudentRequest studentRequest) throws MessagingException {
        return studentService.save(studentRequest);
    }

    @Operation(summary = "Получить все студенты!",
            description = "Метод для получение всу студенты с пагинацией !" +
                          " Авторизация: администратор и инструктор!")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @GetMapping("/findAll")
    public AllStudentResponse findAll(
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "6") int size,
            @RequestParam(required = false, defaultValue = "") String search,
            @RequestParam(required = false, defaultValue = "") String studyFormat,
            @RequestParam(required = false, defaultValue = "") Long groupId
    ) {
        return studentService.findAll(search, studyFormat, groupId, page, size);
    }

    @Operation(summary = "Получить все студенты!",
            description = "Метод для получение всe студенты по их group_id с пагинацией!" +
                          " Авторизация: администратор и инструктор!")
    @PreAuthorize("hasAnyAuthority('ADMIN','INSTRUCTOR')")
    @GetMapping("/findAllGroupStud/{groupId}")
    public AllStudentResponse findAllGroupStud(@RequestParam(required = false, defaultValue = "1") int page,
                                               @RequestParam(required = false, defaultValue = "6") int size,
                                               @PathVariable Long groupId) {
        return studentService.findAllGroupStud(page, size, groupId);
    }

    @Operation(summary = "Получить информацию о студенте по идентификатору ",
            description = "Метод для получения информации о студенте по его идентификатору." +
                          " Авторизация: администратор и инструктор!")
    @PreAuthorize("hasAnyAuthority('ADMIN','INSTRUCTOR')")
    @GetMapping("/findById/{studId}")
    public StudentResponse findById(@PathVariable Long studId) {
        return studentService.findById(studId);
    }

    @Operation(summary = "Обновить информацию о студенте",
            description = "Метод для обновления информации о студенте по его идентификатору." +
                          " Авторизация: администратор и инструктор!")
    @PreAuthorize("hasAnyAuthority('ADMIN','INSTRUCTOR')")
    @PutMapping("/update/{studId}")
    public SimpleResponse update(@PathVariable Long studId,
                                 @RequestBody StudentRequest studentRequest) {
        return studentService.update(studId, studentRequest);
    }

    @Operation(summary = "Удалить cтудента",
            description = "Метод для удаления cтудента по его идентификатору." +
                          " Авторизация: администратор и инструктор!")
    @PreAuthorize("hasAnyAuthority('ADMIN','INSTRUCTOR')")
    @GetMapping("/{studId}")
    public SimpleResponse delete(@PathVariable Long studId, Principal principal) {
        System.err.println("principal.getName() = " + principal.getName());
        System.err.println("\"start\" = " + "start");
        return studentService.delete(studId);
    }

    @Operation(description = "Импортировать студентов в группу")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @PostMapping(value = "/importStudents/{groupId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public SimpleResponse importStudents(@PathVariable Long groupId, @RequestPart("file") @Valid MultipartFile file) {
        return studentService.importStudentsFromExcel(groupId, file);
    }
}
