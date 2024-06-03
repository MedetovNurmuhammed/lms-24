package lms.dto.response;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnswerTaskRatingResponse {
    private Long id;
    private int point;
}
