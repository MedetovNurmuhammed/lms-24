package lms.dto.response;

import lms.enums.Role;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record CommentResponse(
        String author,
        Role role,
        String content,
        LocalDateTime dateTime) {
}
