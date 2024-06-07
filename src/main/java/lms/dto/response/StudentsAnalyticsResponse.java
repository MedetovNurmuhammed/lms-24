package lms.dto.response;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class StudentsAnalyticsResponse {
    private int students;
    private int graduated;
    private int  total;
}
