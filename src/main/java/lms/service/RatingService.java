package lms.service;

import lms.dto.response.RatingResponse;
import lms.dto.response.StudentRatingResponse;
import lms.dto.response.StudentTopRating;

import java.security.Principal;

public interface RatingService {
    RatingResponse findAllStudentRating(Long courseId);

    StudentRatingResponse findMyRating(Principal principal, Long courseId);

    StudentTopRating findAllRatings(Long courseId);
}
