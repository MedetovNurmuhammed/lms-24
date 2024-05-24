package lms.dto.request;

import jakarta.validation.constraints.NotBlank;
import lms.validation.email.EmailValidation;
import lms.validation.phoneNumber.PhoneNumberValidation;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InstructorRequest {
    @NotBlank
    private String firstName;
    @NotBlank
    private String lastName;
    @PhoneNumberValidation
    private String phoneNumber;
    @EmailValidation
    private String email;
    @NotBlank
    private String specialization;
}
