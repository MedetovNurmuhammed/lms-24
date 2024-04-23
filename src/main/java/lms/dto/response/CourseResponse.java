package lms.dto.response;

import lombok.Builder;

@Builder
public record CourseResponse(
        Long id,
        String courseName
) {
}
