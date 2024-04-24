package lms.dto.request;


import com.poiji.annotation.ExcelCell;
import com.poiji.annotation.ExcelSheet;
import lms.enums.StudyFormat;
import lombok.Data;

@Data
@ExcelSheet("users")
public class ExcelUser {
    @ExcelCell(0)
    private String fullName;
    @ExcelCell(1)

    private String email;
    @ExcelCell(2)

    private String phoneNumber;
    @ExcelCell(3)

    private StudyFormat studyFormat;
}
