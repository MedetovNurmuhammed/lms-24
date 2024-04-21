package lms.dto.response;

import lombok.Builder;
import java.util.List;
@Builder
public record AllInstructorResponse(
        List<InstructorResponse> instructorResponses
){}
