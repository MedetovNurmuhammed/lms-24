package lms.dto.response;

import lombok.Builder;

@Builder
public record InstructorNamesResponse(
        Long Id,
        String instructorName
) {
}
