package lms.dto.response;

import lombok.Builder;

@Builder
public record LessonResponse(
        Long id,
        String title
) {

}
