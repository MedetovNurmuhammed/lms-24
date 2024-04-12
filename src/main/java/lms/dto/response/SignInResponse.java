package lms.dto.response;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lms.enums.Role;
import lombok.Builder;
import org.springframework.http.HttpStatus;

@Builder
public record SignInResponse(Long id,
                             String token,
                             String email,
                             @Enumerated(EnumType.STRING)
                             Role role,
                             HttpStatus httpStatus,
                             String message) {
}
