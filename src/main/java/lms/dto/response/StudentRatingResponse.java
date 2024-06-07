package lms.dto.response;

import jakarta.validation.constraints.Future;
import lombok.*;

import java.util.List;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StudentRatingResponse {
    private Long id;
    private   String fullName;
    private List<LessonRatingResponse> lessonRatingResponses;
    private double totalScore;
    private double completionPercentage;
}