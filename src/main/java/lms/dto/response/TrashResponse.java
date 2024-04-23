package lms.dto.response;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lms.enums.Type;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder
public class TrashResponse {
    private Long id;
    @Enumerated(EnumType.STRING)
    private Type type;
    private String name;
    private ZonedDateTime dateOfDelete;
}
