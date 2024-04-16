package lms.api;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lms.dto.request.CourseRequest;
import lms.dto.response.FindAllResponseCourse;
import lms.dto.response.SimpleResponse;
import lms.service.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/course")
public class CourseApi {
    private final CourseService courseService;

    @PostMapping("/createCourse")
    @Operation(description = "Create course")
    public SimpleResponse createCourse(@RequestBody @Valid CourseRequest courseRequest) {
        return courseService.createCourse(courseRequest);
    }

    @Secured("ADMIN")
    @PutMapping("/updateCourse/{courseId}")
    @Operation(description = "Update Course")
    public SimpleResponse editCourse(@PathVariable Long courseId,
                                     @RequestBody @Valid CourseRequest courseRequest) {
        return courseService.editCourse(courseId, courseRequest);
    }

    @Secured("ADMIN")
    @DeleteMapping("/deleteCourse/{courseId}")
    @Operation(description = "Delete Course")
    public SimpleResponse deleteCourseById(@PathVariable Long courseId) {
        return courseService.deleteCourseById(courseId);
    }

    @Secured("ADMIN")
    @GetMapping("/findAllCourse")
    @Operation(description = "Find All")
    public Page<FindAllResponseCourse> findAllCourse(@RequestParam int page,
                                                     @RequestParam int size) {
        return courseService.findAllCourse(page, size);
    }

    @Secured("ADMIN")
    @PostMapping("/assignInGroupToCourse/{groupId}/{courseId}")
    @Operation(description = "AssignIn group to course")
    public SimpleResponse assignInGroupToCourse(@PathVariable Long groupId, @PathVariable Long courseId) {
        return courseService.assignInGroupToCourse(groupId, courseId);
    }

    @Secured("ADMIN")
    @PostMapping("/assignInInstructorToCourse/{courseId}/{instructorId}")
    @Operation(description = "AssignIn Instructor to Course")
    public SimpleResponse assignInstructorsToCourse(@PathVariable Long courseId, @RequestParam List<Long> instructorIds) {
        return courseService.assignInstructorsToCourse(courseId, instructorIds);

    }
}


