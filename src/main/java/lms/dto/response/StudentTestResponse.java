package lms.dto.response;

public record StudentTestResponse(
        Long resultTestId,
        String fullName,
        StatusAnswerTest status,
        double point
) {

}
