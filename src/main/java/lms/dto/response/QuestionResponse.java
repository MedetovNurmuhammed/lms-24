package lms.dto.response;

import lombok.Builder;

import java.util.List;

@Builder
public record QuestionResponse(
     Long id,
     String title,
     List<OptionResponse>optionResponses
) {
}
