package lms.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class LessonRequest {
    @NotBlank
    private String title;
    @NotNull
    private LocalDate createdAt;

}
