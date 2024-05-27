package lms.repository;

import lms.dto.response.LessonResponse;
import lms.entities.Lesson;
import lms.entities.Option;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LessonRepository extends JpaRepository<Lesson, Long> {
    @Query("select new lms.dto.response.LessonResponse(i.id, i.title, i.createdAt) from Lesson i where i.course.id = :courseId order by i.createdAt asc")
    List<LessonResponse> findLessons(@Param("courseId") Long courseId);
    default Page<LessonResponse> findAllLessons(Pageable pageable , Long courseId){
        List<LessonResponse> lessons = findLessons(courseId);
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), lessons.size());
        return new PageImpl<>(lessons.subList(start, end), pageable, lessons.size());
    }

    @Query("select s from Lesson s where s.id =:lessonId")
    <T> ScopedValue<lms.entities.Lesson> findLesson(Long lessonId);
}
