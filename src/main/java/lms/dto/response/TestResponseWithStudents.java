package lms.dto.response;

import lombok.Builder;

import java.util.List;
@Builder
public record TestResponseWithStudents(
        Long id,
        List<StudentTestResponse> studentTestResponses
) {
}
