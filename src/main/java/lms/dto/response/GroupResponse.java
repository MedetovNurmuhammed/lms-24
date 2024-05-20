package lms.dto.response;

import lombok.Builder;

import java.time.LocalDate;

@Builder
public record  GroupResponse(
        Long id,
        String title,
        String description,
        String image,
        LocalDate dateOfStart,
        LocalDate dateOfEnd
) {
}
