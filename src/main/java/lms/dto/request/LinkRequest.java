package lms.dto.request;

import jakarta.validation.constraints.NotBlank;

public record LinkRequest (
        @NotBlank
        String title,
        @NotBlank
        String url
){
}
