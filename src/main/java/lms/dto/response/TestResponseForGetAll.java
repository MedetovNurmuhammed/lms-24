package lms.dto.response;

import lombok.Builder;

@Builder
public record TestResponseForGetAll (
        Long testId,
        String title,
        int hour,
        int minute
){
}
