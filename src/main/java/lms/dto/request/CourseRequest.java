package lms.dto.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Builder
@Data
public class CourseRequest {
    String image;
    @NotBlank
    String title;
    @NotBlank
    String description;
    @NotNull
    @Future(message = "Дата окончания не должна быть в прошедшим")
    LocalDate dateOfEnd;
}
