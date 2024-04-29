package lms.dto.response;

import lombok.Builder;
import java.util.List;

@Builder
public record AllAnnouncementOfStudentResponse (
        List<AnnouncementOfStudent> announcements
) {
}
