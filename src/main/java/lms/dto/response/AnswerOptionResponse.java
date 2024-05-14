package lms.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AnswerOptionResponse {
    private Long optionId;
    private String option;
    private boolean isTrue;
    private boolean yourChoice;

}
