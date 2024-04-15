package lms.dto.response;

import lombok.Builder;

@Builder
public record InstructorResponse(
        Long id,
        String fullName,
        String specialization,
        String phoneNumber,
        String email,
        String password

) {

}
