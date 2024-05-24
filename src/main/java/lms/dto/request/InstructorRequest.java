package lms.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lms.validation.password.PasswordValidation;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InstructorRequest {
    @NotBlank(message = "Имя не может быть пустым.")
    private String firstName;
    @NotBlank(message = "Фамилия не может быть пустой.")
    private String lastName;
    @PasswordValidation
    private String phoneNumber;
    @Email(message = "Некорректный адрес электронной почты.")
    private String email;
    @NotBlank(message = "Специализация не может быть пустой.")
    private String specialization;
}
