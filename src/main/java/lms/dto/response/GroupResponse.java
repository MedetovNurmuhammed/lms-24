package lms.dto.response;

import lms.entities.Student;
import lombok.Builder;
import org.springframework.data.domain.Page;

import java.util.List;

@Builder
public record GroupResponse(
        Long id,
        String title,
        Page<StudentResponse> students
) {
}
