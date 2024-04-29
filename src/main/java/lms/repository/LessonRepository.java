package lms.repository;

import lms.dto.response.LessonResponse;
import lms.entities.Lesson;
import lms.exceptions.NotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LessonRepository extends JpaRepository<Lesson, Long> {
    @Query("select new lms.dto.response.LessonResponse(i.id, i.title, i.createdAt) from Lesson i where i.course.id = :courseId order by i.createdAt asc")
    List<LessonResponse> findLessons(@Param("courseId") Long courseId);
    default Page<LessonResponse> findAllLessons(Pageable pageable , Long courseId){
        List<LessonResponse> lessons = findLessons(courseId);
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), lessons.size());
        return new PageImpl<>(lessons.subList(start, end), pageable, lessons.size());
    }

    default Lesson takeById(Long id){
        return findById(id).orElseThrow(() -> new NotFoundException("not found"));
    }

    @Query("select l from  Lesson l where l.id = :lessonId")
    Lesson getLessonById(Long lessonId);
}
