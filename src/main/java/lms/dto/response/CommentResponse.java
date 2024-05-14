package lms.dto.response;

import lms.enums.Role;
import lombok.Builder;

@Builder
public record CommentResponse(
        String author,
        Role role,
        String content) {
}
