package lms.dto.response;

import java.util.List;

public record AllAnnouncementResponse(
        int page,
        int size,
        List<AnnouncementResponse> announcements
) {
}
