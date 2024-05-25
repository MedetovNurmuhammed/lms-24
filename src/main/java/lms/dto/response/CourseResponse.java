package lms.dto.response;

import lombok.Builder;

import java.time.LocalDate;

@Builder
public record CourseResponse(
        Long id,
        String title,
        String description,
        String image,
        LocalDate dateOfEnd
        ) {
}
