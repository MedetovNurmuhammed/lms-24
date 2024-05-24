package lms.dto.response;

import lombok.Builder;

@Builder
public record AwsResponse(
        String fileName,
        String urlFile
) {
}
