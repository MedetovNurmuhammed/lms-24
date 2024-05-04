package lms.dto.response;

import lombok.Builder;

@Builder
public record PresentationResponse(Long id,
                                   String title,
                                   String description,
                                   String file) {
}
