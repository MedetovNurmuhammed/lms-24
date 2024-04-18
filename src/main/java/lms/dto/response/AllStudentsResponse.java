package lms.dto.response;

import lombok.Builder;
import java.util.List;

@Builder
public record AllStudentsResponse(
        List<StudentResponse> studentResponses
){}
