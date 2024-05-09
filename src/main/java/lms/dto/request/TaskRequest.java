package lms.dto.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.List;

public record TaskRequest(
        @NotNull
        String title,
        @NotNull
        String description,
        String file,
        String image,
        String code,
        @Future
        LocalDateTime deadline,
        List<String> links
) {
}
