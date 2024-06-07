package lms.dto.response;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class CourseAnalyticsResponse {
    private  int courses;
    private  int groups;
    private int instructors;
}
