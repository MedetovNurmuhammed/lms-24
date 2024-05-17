package lms.dto.request;

public record UpdateOptionRequest(
        Long optionId,
        String option,
        boolean isTrue
) {
}
