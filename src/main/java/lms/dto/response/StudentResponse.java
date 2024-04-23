package lms.dto.response;

import lms.enums.StudyFormat;
import lms.enums.Type;
import lombok.Builder;

@Builder
public record StudentResponse(
        Long id,
        Type type,
        String fullName,
        String phoneNumber,
        String groupName,
        StudyFormat studyFormat,
        String email
) {
}
