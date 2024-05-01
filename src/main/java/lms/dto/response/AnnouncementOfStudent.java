package lms.dto.response;

import lombok.Builder;

@Builder
public record AnnouncementOfStudent(
     long announcementId,
     String content,
     Boolean isView
) {
}
