package lms.dto.response;

import lombok.Builder;
import java.time.LocalDate;

@Builder
public record VideoResponse(
        Long id,
        String titleOfVideo,
        String description,
        String linkOfVideo,
        LocalDate createdAt
) {
}
