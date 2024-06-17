package lms.dto.response;

import lms.enums.Type;
import lombok.*;
import java.time.ZonedDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder
public class TrashResponse {
    private Long id;
    private Type type;
    private String name;
    private ZonedDateTime dateOfDelete;
}
