package lms.dto.response;

import lombok.Builder;

import java.util.List;
@Builder
public record FindAllPresentationResponse(int page,
                                          int size,
                                          List<PresentationResponse>presentationResponseList) {
}
