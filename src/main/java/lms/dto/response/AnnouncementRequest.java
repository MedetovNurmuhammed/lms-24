package lms.dto.response;

import jakarta.validation.constraints.*;

import java.time.LocalDate;
import java.util.List;

public record AnnouncementRequest(
        @NotBlank
        String announcementContent,
        @FutureOrPresent(message = "Expiration date must be in the present or future")
        LocalDate expirationDate,
        @NotNull(message = "Target group IDs must not be null")
        @Size(min = 1, message = "At least one target group ID must be provided")
        List<Long> targetGroupIds,
        @NotNull(message = "Published date must not be null")
        @FutureOrPresent(message = "Expiration date must be in the present or future")
        LocalDate publishedDate
) {
}
