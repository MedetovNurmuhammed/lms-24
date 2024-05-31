package lms.service.impl;

import jakarta.transaction.Transactional;
import lms.dto.response.CourseAnalyticsResponse;
import lms.dto.response.StudentsAnalyticsResponse;
import lms.repository.CourseRepository;
import lms.repository.GroupRepository;
import lms.repository.InstructorRepository;
import lms.repository.StudentRepository;
import lms.service.AnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class AnalyticsServiceImpl implements AnalyticsService {
    private final CourseRepository courseRepository;
    private final GroupRepository groupRepository;
    private final InstructorRepository instructorRepository;
    private final StudentRepository studentRepository;

    @Override
    public StudentsAnalyticsResponse getAllStudentsCount() {
        int total = studentRepository.totalStudents();
        return null;
    }

    @Override
    public CourseAnalyticsResponse getAllCoursesCount() {
        int countCourse = courseRepository.getAllCourseCount();
        int countGroup = groupRepository.getAllGroupsCount();
        int countInstructors = instructorRepository.getAllInstructorsCount();
        return CourseAnalyticsResponse.builder()
                .courses(countCourse)
                .groups(countGroup)
                .instructors(countInstructors)
                .build();
    }
}
