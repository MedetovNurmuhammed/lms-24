package lms.dto.response;

import lombok.Builder;

import java.time.LocalDate;
import java.util.List;

@Builder
public record TaskResponse(
    Long id,
    String title,
    String description,
    String file,
    String image,
    String code,
    LocalDate deadline,
    List<String> links
    ) {
}
