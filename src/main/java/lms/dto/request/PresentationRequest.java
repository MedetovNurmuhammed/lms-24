package lms.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class PresentationRequest {
    @NotBlank
    private String title;
    @NotBlank
    private String description;
    @NotBlank
    private String file;

}
