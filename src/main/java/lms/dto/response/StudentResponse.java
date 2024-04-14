package lms.dto.response;

import lms.enums.StudyFormat;
import lombok.Builder;

@Builder
public record StudentResponse(
        String fullName,
        StudyFormat studyFormat,
        String phoneNumber,
        String email
) {
}
