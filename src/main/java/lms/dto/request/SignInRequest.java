package lms.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lms.enums.StudyFormat;
import lms.validation.email.EmailValidation;
import lms.validation.phoneNumber.PhoneNumberValidation;
import lombok.Data;

@Data
public class SignInRequest {
    @NotBlank
    private String login;
    @NotBlank
    private String password;


}
