package lms.dto.response;

import lombok.Builder;

import java.util.List;

@Builder
public record AllTestResponse(
   List<TestResponseForGetAll> testResponseForGetAll
) {
}
