package lms.dto.request;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
@Data
@Builder
public class ExamRequest {
    @NotNull
    private String title;
    @FutureOrPresent
    private LocalDate examDate;
}
