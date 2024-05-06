package lms.dto.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EditPresentationRequest {
    private String title;
    private String description;
    private String file;
}
