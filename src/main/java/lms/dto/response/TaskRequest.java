package lms.dto.response;

import java.time.LocalDate;

public record TaskRequest(
        String title,
        String description,
        String file,
        String image,
        String code,
        LocalDate deadline
) {
}
