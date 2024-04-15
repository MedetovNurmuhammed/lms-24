package lms.dto.request;

import lombok.Builder;

@Builder
public record CreatePasswordRequest(
        String token,
        String password
) {
}
