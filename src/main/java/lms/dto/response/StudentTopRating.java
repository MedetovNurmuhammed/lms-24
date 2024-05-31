package lms.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class StudentTopRating {
    private List<StudentsRatingResponse> studentsRatingResponseList;
}
