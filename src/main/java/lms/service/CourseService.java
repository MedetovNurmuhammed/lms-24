package lms.service;

import lms.dto.request.CourseRequest;
import lms.dto.response.AllInstructorsOrStudentsOfCourse;
import lms.dto.response.FindAllResponseCourse;
import lms.dto.response.SimpleResponse;
import lms.enums.Role;

import java.util.List;

public interface CourseService {

    SimpleResponse createCourse(CourseRequest courseRequest);

    SimpleResponse editCourse(Long courseId, CourseRequest courseRequest);

    SimpleResponse deleteCourseById(Long courseId);

    FindAllResponseCourse findAllCourse(int page, int size);

    SimpleResponse assignInGroupToCourse(Long groupId, Long courseId);

    SimpleResponse assignInstructorsToCourse(Long courseId, List<Long> instructorIds);

    AllInstructorsOrStudentsOfCourse findAllInstructorsOrStudentsByCourseId(int page, int size, Long courseId, Role role);
}
