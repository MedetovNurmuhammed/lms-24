package lms.dto.request;

import java.time.Duration;
import java.util.List;
import java.util.Map;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;


public record TestRequest(
        @NotNull
        String title,
        @Min(0) @Max(24)
        int hour,
        @Min(1) @Max(60)
        int minute,
        Map<QuestionRequest, List<OptionRequest>> questionRequests
) {}
