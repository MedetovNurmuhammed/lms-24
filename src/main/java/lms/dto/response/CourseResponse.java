package lms.dto.response;

import lombok.Builder;
import lombok.Getter;

@Builder
public record CourseResponse(
        Long id,
        String courseName
) {
}
