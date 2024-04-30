package lms.dto.request;

import java.time.LocalDate;
import java.util.List;

public record TaskRequest(
        String title,
        String description,
        String file,
        String image,
        String code,
        LocalDate deadline,
        List<String> links
) {
}
