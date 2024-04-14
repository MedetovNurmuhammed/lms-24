package lms.service;

import lms.dto.request.CourseRequest;
import lms.dto.response.FindAllResponseCourse;
import lms.dto.response.SimpleResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface CourseService {

    SimpleResponse createCourse(CourseRequest courseRequest);

    SimpleResponse editCourse(Long courseId, CourseRequest courseRequest);

    SimpleResponse deleteCourseById(Long courseId);

    Page<FindAllResponseCourse> findAllCourse(int page, int size);

    SimpleResponse assignInGroupToCourse(Long groupId, Long courseId);

    SimpleResponse assignInInstructorToCourse(Long courseId, Long instructorId);
}
