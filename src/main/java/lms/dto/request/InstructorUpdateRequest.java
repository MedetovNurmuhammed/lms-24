package lms.dto.request;

import jakarta.validation.constraints.NotBlank;
import lms.validation.phoneNumber.PhoneNumberValidation;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InstructorUpdateRequest {
    @NotBlank
    private String firstName;
    @NotBlank
    private String lastName;
    @PhoneNumberValidation
    private String phoneNumber;
    @NotBlank
    private String specialization;
}
