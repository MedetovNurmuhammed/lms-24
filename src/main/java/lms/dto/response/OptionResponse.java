package lms.dto.response;

public record OptionResponse(
        Long optionId,
        String option,
        Boolean isTrue
) {
}
