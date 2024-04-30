package lms.dto.request;

import java.time.LocalDateTime;
import java.util.List;

public record TaskRequest(
        String title,
        String description,
        String file,
        String image,
        String code,
        LocalDateTime deadline,
        List<String> links
) {
}
