package lms.dto.response;

import java.time.LocalDate;
import java.util.List;

public record AnnouncementRequest(
        String announcementContent,
        LocalDate expirationDate,
        List<Long> targetGroupIds,
        Long authorId,
        LocalDate publishedDate) {
}
