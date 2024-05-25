package lms.dto.response;

import lombok.Builder;
import java.time.LocalDate;
import java.util.List;

@Builder
public record FindAllResponseCourse(
        int page,
        int size,
        List<CourseResponse> courses
) {
}
