package lms.dto.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record GroupRequest(
        @NotBlank(message = "Название не должно быть пустым")
        String title,
        @NotBlank(message = "Описание не должно быть пустым")
        String description,
        @NotNull(message = "Изображение не должно быть пустым")
        String image,
        @Future(message = "Дата окончания должна быть в будущем")
        LocalDate dateOfEnd) {
}
