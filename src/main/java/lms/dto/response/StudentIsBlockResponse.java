package lms.dto.response;

import lombok.Builder;
import org.springframework.http.HttpStatus;
@Builder
public record StudentIsBlockResponse(
        Boolean isTrue,
        String fullName,
        HttpStatus httpStatus,
        String message
) {
}
