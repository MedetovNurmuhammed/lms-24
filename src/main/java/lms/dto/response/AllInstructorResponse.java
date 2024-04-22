package lms.dto.response;

import lombok.Builder;
import java.util.List;
@Builder
public record AllInstructorResponse(
        int page,
        int size,
        List<InstructorResponse> instructorResponses
){}
