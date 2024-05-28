package lms.dto.request;

import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.util.List;

public record AnnouncementRequest(
        @NotBlank(message = "Содержимое объявления не должно быть пустым")
        String announcementContent,
        @Future(message = "Дата истечения срока должна быть в будущем ")
        LocalDate expirationDate,
        @NotNull(message = "Идентификаторы целевой группы не должны быть пустыми")
        @Size(min = 1, message = "Необходимо указать хотя бы один идентификатор целевой группы")
        List<Long> targetGroupIds,
        @NotNull(message = "Дата публикации не должна быть пустой")
        @FutureOrPresent(message = "Дата публикации должна быть в настоящем или будущем")
        LocalDate publishedDate
) {
}
