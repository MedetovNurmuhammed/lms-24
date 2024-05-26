package lms.repository;

import lms.dto.response.LessonResponse;
import lms.entities.Lesson;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface LessonRepository extends JpaRepository<Lesson, Long> {
    @Query("select new lms.dto.response.LessonResponse(i.id, i.title, i.createdAt) from Lesson i where i.course.id = :courseId order by i.createdAt asc")
    Page<LessonResponse> findAllLessons(@Param("courseId") Long courseId,Pageable pageable);

}
