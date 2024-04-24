package lms.dto.response;

import lombok.Builder;
import java.util.List;

@Builder
public record FindByIdInstructorResponse(
        Long id,
        String fullName,
        String specialization,
        String phoneNumber,
        String email,
        List<String>courseNames
) {
}
