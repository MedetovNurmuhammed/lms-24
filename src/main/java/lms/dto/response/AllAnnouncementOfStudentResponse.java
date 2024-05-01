package lms.dto.response;

import lombok.Builder;
import java.util.List;

@Builder
public record AllAnnouncementOfStudentResponse (
        int size,
        int page,
        List<AnnouncementOfStudent> announcements
) {
}
