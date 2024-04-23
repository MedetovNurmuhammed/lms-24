package lms.api;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lms.dto.request.InstructorRequest;
import lms.dto.request.InstructorUpdateRequest;
import lms.dto.response.AllInstructorResponse;
import lms.dto.response.FindByIdInstructorResponse;
import lms.dto.response.SimpleResponse;
import lms.service.InstructorService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/instructors")
public class InstructorApi {
    private final InstructorService instructorService;

    @Secured("ADMIN")
    @Operation(summary = "добавляет инструктора.(Авторизация: администратор)")
    @PostMapping("/addInstructor")
    public SimpleResponse addInstructor(@RequestBody InstructorRequest instructorRequest) throws MessagingException {
        return instructorService.addInstructor(instructorRequest);
    }

    @Secured("ADMIN")
    @Operation(summary = "Возвращает пагинированный список всех инструкторов.(Авторизация: администратор)")
    @GetMapping("/findAll")
    public AllInstructorResponse findAll(@RequestParam(required = false, defaultValue = "1") int page,
                                         @RequestParam(required = false, defaultValue = "6") int size) {
        return instructorService.findAll(page, size);
    }

    @Secured("ADMIN")
    @Operation(summary = "Возвращает инструктора.(Авторизация: администратор)")
    @GetMapping("/findById/{instructorId}")
    public FindByIdInstructorResponse findById(@PathVariable Long instructorId) {
        return instructorService.findById(instructorId);
    }

    @Secured("ADMIN")
    @PutMapping("/updateInstructor/{instructorId}")
    @Operation(summary = "Обновляет информацию о инструкторе.(Авторизация: администратор)")
    public SimpleResponse update(
            @RequestBody @Valid InstructorUpdateRequest instructorRequest, @PathVariable Long instructorId) {
        return instructorService.update(instructorRequest, instructorId);
    }

    @Secured("ADMIN")
    @DeleteMapping("/delete/{instructorId}")
    @Operation(summary = "Удаляет текущего инструкторa.(Авторизация: администратор)")
    public SimpleResponse delete(@PathVariable Long instructorId) {
        return instructorService.delete(instructorId);
    }
}

