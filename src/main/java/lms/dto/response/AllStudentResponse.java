package lms.dto.response;

import lombok.Builder;

import java.util.List;

@Builder
public record AllStudentResponse(
        int page ,
        int size,
        List<StudentResponse> students
) {
}
