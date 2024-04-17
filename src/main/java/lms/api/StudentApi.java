package lms.api;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.mail.MessagingException;
import lms.dto.request.StudentRequest;
import lms.dto.response.AllStudentsResponse;
import lms.dto.response.SimpleResponse;
import lms.dto.response.StudentResponse;
import lms.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;

@RestController
@RequestMapping("/api/students")
@RequiredArgsConstructor
public class StudentApi {
    private final StudentService studentService;

    @Secured("ADMIN")
    @PostMapping("/save")
    @Operation(summary = "Сохранить студента",
            description = "Метод для сохранение студента и отправка сообщение почту чтобы создать студент создал себе пароль!")
    public SimpleResponse saveStudent(@RequestBody StudentRequest studentRequest) throws MessagingException {
        return studentService.save(studentRequest);
    }

    @Operation(summary = "Получить все студенты!",
            description = "Метод для получение всу студенты с пагинацией !")
    @PreAuthorize("hasAnyAuthority('ADMIN','INSTRUCTOR')")
    @GetMapping("/findAll")
    public AllStudentsResponse findAll(@RequestParam int page,
                                       @RequestParam int size) {
        return studentService.findAll(page, size);
    }

    @Operation(summary = "Получить все студенты!",
            description = "Метод для получение всу студенты по их group_id с пагинацией!")
    @PreAuthorize("hasAnyAuthority('ADMIN','INSTRUCTOR')")
    @GetMapping("/findAllGroupStud/{groupId}")
    public AllStudentsResponse findAllGroupStud(@RequestParam int page,
                                                @RequestParam int size,
                                                @PathVariable Long groupId) {
        return studentService.findAllGroupStud(page, size, groupId);
    }

    @Operation(summary = "Получить информацию о студенте по идентификатору",
            description = "Метод для получения информации о студенте по его идентификатору")
    @PreAuthorize("hasAnyAuthority('ADMIN','INSTRUCTOR')")
    @GetMapping("/findById/{studId}")
    public StudentResponse findById(@PathVariable Long studId){
        return studentService.findById(studId);
    }

    @Operation(summary = "Обновить информацию о студенте",
            description = "Метод для обновления информации о студенте по его идентификатору ")
    @PreAuthorize("hasAnyAuthority('ADMIN','INSTRUCTOR')")
    @PutMapping("/update/{studId}")
    public SimpleResponse update(@PathVariable Long studId,
                                 @RequestBody StudentRequest studentRequest) {
        return studentService.update(studId, studentRequest);
    }

    @Operation(summary = "Удалить cтудента",
            description = "Метод для удаления cтудента по его идентификатору")
    @PreAuthorize("hasAnyAuthority('ADMIN','INSTRUCTOR')")
    @DeleteMapping("/delete/{studId}")
    public SimpleResponse delete(@PathVariable Long studId) {
        return studentService.delete(studId);
    }
}
