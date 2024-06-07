package lms.service;

import lms.dto.response.CourseAnalyticsResponse;
import lms.dto.response.StudentsAnalyticsResponse;

public interface AnalyticsService {
    StudentsAnalyticsResponse getAllStudentsCount();

    CourseAnalyticsResponse getAllCoursesCount();
}
