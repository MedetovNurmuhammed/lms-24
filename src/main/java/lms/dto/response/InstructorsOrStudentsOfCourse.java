package lms.dto.response;

import lombok.Builder;

@Builder
public record InstructorsOrStudentsOfCourse(
        Long id,
        String courseName,
        String fullName,
        String specializationOrStudyFormat,
        String phoneNumber,
        String email,
        Boolean isBlock
) {
}
