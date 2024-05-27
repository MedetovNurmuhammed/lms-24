package lms.dto.request;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LessonRequest {
    @NotBlank(message = "Заголовок не должен быть пустым")
    private String title;
    @FutureOrPresent(message = "Дата создания должна быть в настоящем или будущем времени")
    private LocalDate createdAt;

}
