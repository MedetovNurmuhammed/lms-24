package lms.api;

import jakarta.mail.MessagingException;
import lms.dto.request.StudentRequest;
import lms.dto.response.AllStudentsResponse;
import lms.dto.response.SimpleResponse;
import lms.service.StudentService;
import lms.validation.password.PasswordValidation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/students")
@RequiredArgsConstructor
@Slf4j
public class StudentApi {
    private final StudentService studentService;
    @Secured("ADMIN")
    @PostMapping("/save")
    public SimpleResponse saveStudent(@RequestBody StudentRequest studentRequest) throws MessagingException {
        log.info("Success saved "+studentRequest.email());
        return studentService.save(studentRequest);
    }

    @PreAuthorize("hasAuthority('STUDENT')")
    @PostMapping("/createPassword")
    public SimpleResponse createPassword(@RequestParam  @PasswordValidation String password){
        log.info("Password created for !");
        return studentService.createPassword(password);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','INSTRUCTOR')")
    @GetMapping("/findAll")
    public AllStudentsResponse findAll(@RequestParam int page,
                                       @RequestParam int size){
        return studentService.findAllWithoutBlock(page,size);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','INSTRUCTOR')")
    @GetMapping("/findAllGroupStud/{groupId}")
    public AllStudentsResponse findAllGroupStud(@RequestParam int page,
                                                @RequestParam int size,
                                                @PathVariable Long groupId){
        return studentService.findAllGroupStud(page,size,groupId);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','INSTRUCTOR')")
    @PutMapping("/update/{studId}")
    public SimpleResponse update(@PathVariable Long studId,
                                 @RequestBody StudentRequest studentRequest){
        return studentService.update(studId,studentRequest);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','INSTRUCTOR')")
    @DeleteMapping("/delete/{studId}")
    public SimpleResponse delete(@PathVariable Long studId){
        return studentService.delete(studId);
    }
}
