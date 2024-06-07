package lms.api;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lms.dto.request.InstructorRequest;
import lms.dto.request.InstructorUpdateRequest;
import lms.dto.response.AllInstructorResponse;
import lms.dto.response.FindByIdInstructorResponse;
import lms.dto.response.InstructorNamesResponse;
import lms.dto.response.SimpleResponse;
import lms.service.InstructorService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/instructors")
@CrossOrigin(origins = "*",maxAge = 3600)
public class InstructorApi {
    private final InstructorService instructorService;

    @Secured("ADMIN")
    @Operation(summary = "добавляет инструктора.(Авторизация: администратор)")
    @PostMapping()
    public SimpleResponse addInstructor(@RequestBody @Valid InstructorRequest instructorRequest, @RequestParam String linkForPassword) throws MessagingException {
        return instructorService.addInstructor(instructorRequest, linkForPassword);
    }

    @Secured("ADMIN")
    @Operation(summary = "Возвращает пагинированный список всех инструкторов.(Авторизация: администратор)")
    @GetMapping()
    public AllInstructorResponse findAll(@RequestParam(required = false, defaultValue = "1") int page,
                                         @RequestParam(required = false, defaultValue = "8") int size) {
        return instructorService.findAll(page, size);
    }

    @Secured("ADMIN")
    @Operation(summary = "Возвращает инструктора.(Авторизация: администратор)")
    @GetMapping("/{instructorId}")
    public FindByIdInstructorResponse findById(@PathVariable Long instructorId) {
        return instructorService.findById(instructorId);
    }

    @Secured("ADMIN")
    @PatchMapping("/{instructorId}")
    @Operation(summary = "Обновляет информацию о инструкторе.(Авторизация: администратор)")
    public SimpleResponse update(@RequestBody @Valid InstructorUpdateRequest instructorRequest,
                                 @PathVariable Long instructorId) {
        return instructorService.update(instructorRequest, instructorId);
    }

    @Secured("ADMIN")
    @DeleteMapping("/{instructorId}")
    @Operation(summary = "Удаляет текущего инструкторa.(Авторизация: администратор)")
    public SimpleResponse delete(@PathVariable Long instructorId) {
        return instructorService.delete(instructorId);
    }
    @Secured("ADMIN")
    @Operation(summary = "Возвращает список всех имёнь инструкторов.(Авторизация: администратор)")
    @GetMapping("/allInstructorsName")
    public List<InstructorNamesResponse> allInstructorsName() {
        return instructorService.allInstructorsName();
    }
}

