package lms.dto.response;

import lombok.Builder;

@Builder
public record AllInstructorResponse(
        Long id,
        String fullName,
        String specialization,
        String phoneNumber,
        String email
) {

}
