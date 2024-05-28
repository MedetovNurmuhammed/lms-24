package lms.dto.response;

import lombok.Builder;
import java.util.List;

@Builder
public record AllInstructorsOrStudentsOfCourse(
        int page,
        int size,
        List<InstructorsOrStudentsOfCourse> getAllInstructorsOfCourses,
        List<InstructorsOrStudentsOfCourse> getAllStudentsOfCourses
){}
