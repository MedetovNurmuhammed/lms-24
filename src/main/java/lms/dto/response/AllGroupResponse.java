package lms.dto.response;

import java.time.LocalDate;

public record AllGroupResponse(
        Long id,
        String title,
        String description,
        String image,
        LocalDate dateOfStart,
        LocalDate dateOfEnd
) {
}
