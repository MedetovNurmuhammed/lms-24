package lms.dto.response;

import lombok.Builder;

import java.time.LocalDate;
import java.util.List;
@Builder
public record TestResponse(
        Long id,
        List<StudentTestResponse> studentTestResponses
) {
}
