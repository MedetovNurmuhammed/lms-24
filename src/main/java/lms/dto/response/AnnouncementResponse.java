package lms.dto.response;

import lombok.Builder;
import java.time.LocalDate;

@Builder
public record AnnouncementResponse(
        Long id,
        String content,
        LocalDate publishDate,
        LocalDate endDate,
        Boolean isPublished
) {
}
