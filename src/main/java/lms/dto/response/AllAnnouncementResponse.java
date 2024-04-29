package lms.dto.response;

import lombok.Builder;
import java.util.List;

@Builder
public record AllAnnouncementResponse(
        int page,
        int size,
        List<AnnouncementResponse> announcements
) {
}
