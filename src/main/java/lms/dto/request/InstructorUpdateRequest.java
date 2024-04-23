package lms.dto.request;

import jakarta.validation.constraints.NotBlank;
import lms.validation.email.EmailValidation;
import lms.validation.password.PasswordValidation;
import lms.validation.phoneNumber.PhoneNumberValidation;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class InstructorUpdateRequest {
    @NotBlank
    private String firstName;
    @NotBlank
    private String lastName;
    @PasswordValidation
    private String phoneNumber;
    @EmailValidation
    private String email;
    @NotBlank
    private String specialization;
    private List<Long> courseIds;

}
