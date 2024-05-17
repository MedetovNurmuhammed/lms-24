package lms.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;


public record TestRequest(
        @NotNull
        String title,
        @Min(0) @Max(24)
        int hour,
        @Min(1) @Max(60)
        int minute,
        List<QuestionRequest> questionRequests
) {}
