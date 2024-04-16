package lms.dto.response;

import lombok.Builder;
import org.springframework.data.domain.Page;

@Builder
public record GroupWithStudentsResponse(
        Long id,
        String title
) {
}
