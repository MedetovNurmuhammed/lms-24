package lms.dto.response;

import lombok.Builder;

import java.time.LocalDate;

@Builder
public record LessonResponse(
        Long id,
        String title,
        LocalDate createdAt
) {

}
