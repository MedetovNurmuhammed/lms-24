package lms.dto.response;

import lombok.Builder;
import java.time.LocalDateTime;
import java.util.List;

@Builder
public record TaskResponse(
    Long id,
    String title,
    String description,
    String file,
    String image,
    String code,
    LocalDateTime deadline,
    List<String> links
    ) {
}
