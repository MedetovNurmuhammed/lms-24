package lms.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StudentsRatingResponse {
    private Long id;
    private String fullName;
    private double completionPercentage;
}