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
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/instructors")
@CrossOrigin(origins = "*",maxAge = 3600)
public class InstructorApi {
    private final InstructorService instructorService;

    @Secured("ADMIN")
    @Operation(summary = "добавляет инструктора.(Авторизация: администратор)")
    @PostMapping()
    public SimpleResponse addInstructor(@RequestBody @Valid InstructorRequest instructorRequest) throws MessagingException {
        return instructorService.addInstructor(instructorRequest);
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
    @Operation(summary = "Возвращает пагинированный список всех имёнь инструкторов.(Авторизация: администратор)")
    @GetMapping("allInstructorsName")
    public AllInstructorResponse allInstructorsName(@RequestParam(required = false, defaultValue = "1") int page,
                                         @RequestParam(required = false, defaultValue = "8") int size) {
        return instructorService.allInstructorsName(page, size);
    }
}

