package lms.dto.response;

import lombok.Builder;
import java.util.List;

@Builder
public record AllInstructorsAndStudentsOfCourse(
        int page,
        int size,
        List<InstructorsOrStudentsOfCourse> getAllInstructorsOfCourses,
        List<InstructorsOrStudentsOfCourse> getAllStudentsOfCourses
){}
