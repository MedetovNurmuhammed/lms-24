package lms.api;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lms.dto.request.InstructorRequest;
import lms.dto.request.InstructorUpdateRequest;
import lms.dto.response.AllInstructorResponse;
import lms.dto.response.InstructorResponse;
import lms.dto.response.SimpleResponse;
import lms.service.InstructorService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/instructor")
public class InstructorApi {
    private final InstructorService instructorService;
    @Operation(summary = "добавляет инструктора.")
    @PostMapping("/addInstructor")
    public SimpleResponse addInstructor(@RequestBody InstructorRequest instructorRequest) throws MessagingException {
        return instructorService.addInstructor(instructorRequest);
    }

    @Operation(summary = "Возвращает пагинированный список всех инструкторов.")
    @GetMapping("/findAll")
    public Page<AllInstructorResponse> findAll(@RequestParam(required = false, defaultValue = "1") int page,
                                               @RequestParam(required = false, defaultValue = "6") int size) {
        return instructorService.findAll(page, size);
    }
    @Operation(summary = "Возвращает инструктора.")
    @GetMapping("/findById/{instructorId}")
    public InstructorResponse findById(@PathVariable Long instructorId) {
        return instructorService.findById(instructorId);
    }

    @PutMapping("/updateInstructor/{instructorId}")
    @Operation(summary = "Обновляет информацию о инструкторе.")
    public SimpleResponse update(
            @RequestBody @Valid InstructorUpdateRequest instructorRequest, @PathVariable Long instructorId) {
        return instructorService.update(instructorRequest, instructorId);
    }
    @DeleteMapping("/delete/{instructorId}")
    @Operation(summary = "Удаляет текущего инструкторa.")
    public SimpleResponse delete(@PathVariable Long instructorId) {
        return instructorService.delete(instructorId);
    }
}

