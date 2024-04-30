package lms.dto.response;

import lombok.Builder;
import java.time.LocalDate;
import java.util.List;

@Builder
public record AnnouncementResponse(
        Long id,
        String content,
        String owner,
        List<String> groupNames,
        LocalDate publishDate,
        LocalDate endDate,
        Boolean isPublished
) {
}
