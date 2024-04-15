package lms.api;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lms.dto.request.CreatePasswordRequest;
import lms.dto.request.InstructorRequest;
import lms.dto.request.InstructorUpdateRequest;
import lms.dto.response.InstructorResponse;
import lms.dto.response.PageInstructorResponses;
import lms.dto.response.SimpleResponse;
import lms.service.InstructorService;
import lms.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/instructors")
public class InstructorApi {

    private final UserService userService;
    private final InstructorService instructorService;
    @Operation(summary = "добавляет инструктора.")
    @PostMapping("/addInstructor")
    public SimpleResponse addInstructor(@RequestBody @Valid InstructorRequest instructorRequest) throws MessagingException {
        return instructorService.addInstructor(instructorRequest);
    }
    @Operation(summary = "добавляет password инструктора.")
    @PostMapping("/create/password")
    SimpleResponse createPassword(@RequestBody CreatePasswordRequest createPasswordRequest){
        return instructorService.createPassword(createPasswordRequest.token(), createPasswordRequest.password());
    }



    @Operation(summary = "Возвращает пагинированный список всех инструкторов.")
    @GetMapping("/findAll")
    public PageInstructorResponses findAll(@RequestParam int page,
                                           @RequestParam int size) {
        return instructorService.findAll(page, size);
    }
    @Operation(summary = "Возвращает инструктора.")
    @GetMapping("/findById/{instructorId}")
    public InstructorResponse findAll(@PathVariable Long instructorId) {
        return instructorService.findById(instructorId);
    }
    @Operation(summary = "Возвращает инструктора курса.")
    @GetMapping("/findByCourseId/{courseId}")
    public PageInstructorResponses findAllByCourseId(@PathVariable Long courseId, @RequestParam int page,
                                                     @RequestParam int size) {
        return instructorService.findByCoure(courseId, page, size);
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

