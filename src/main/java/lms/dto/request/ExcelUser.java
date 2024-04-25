package lms.dto.request;


import com.poiji.annotation.ExcelCell;
import com.poiji.annotation.ExcelSheet;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lms.enums.StudyFormat;
import lms.validation.email.EmailValidation;
import lms.validation.phoneNumber.PhoneNumberValidation;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

@Data
@ExcelSheet("users")
@Validated
public class ExcelUser {
    @ExcelCell(0)
    @NotBlank
    @NotNull
    private String fullName;
    @ExcelCell(1)
    @EmailValidation
    @Email
    private String email;
    @ExcelCell(2)
    @PhoneNumberValidation
    private String phoneNumber;
    @ExcelCell(3)
    @NotBlank
    @NotNull
    private StudyFormat studyFormat;
}
