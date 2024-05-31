package lms.api;

import io.swagger.v3.oas.annotations.Operation;
import lms.dto.response.RatingResponse;
import lms.dto.response.StudentRatingResponse;
import lms.dto.response.StudentTopRating;
import lms.service.RatingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/ratingApi")
public class RatingApi {
    private final RatingService ratingService;

    @Secured({"INSTRUCTOR"})
    @GetMapping("/{courseId}")
    @Operation(summary = "Получить рейтинг студентов!", description = "Авторизация: Инструктор!")
    public ResponseEntity<RatingResponse> findAllRatings(@PathVariable Long courseId) {
        RatingResponse response = ratingService.findAllStudentRating(courseId);
        return ResponseEntity.ok(response);

    }
    @Secured("STUDENT")
    @Operation(summary = "Получить свой рейтинг!", description = "Авторизация: Студент!")
    @GetMapping("findAllMyRating/{courseId}")
    public ResponseEntity<StudentRatingResponse> findMyRating(Principal principal, @PathVariable Long courseId) {
        StudentRatingResponse ratingResponse = ratingService.findMyRating(principal, courseId);
        return ResponseEntity.ok(ratingResponse);
    }

    @Secured("STUDENT")
    @Operation(summary = "Получить рейтинг студентов", description = "Авторизация : Студент!")
    @GetMapping("findAllRating/{courseId}")
    public ResponseEntity<StudentTopRating> findAllRatingStudents(@PathVariable Long courseId) {
        StudentTopRating studentsRatingsResponse = ratingService.findAllRatings(courseId);
        return ResponseEntity.ok(studentsRatingsResponse);
    }
}
