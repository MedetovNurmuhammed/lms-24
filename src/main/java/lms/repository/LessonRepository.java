package lms.repository;

import lms.dto.response.InstructorResponse;
import lms.dto.response.LessonResponse;
import lms.entities.Lesson;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface LessonRepository extends JpaRepository<Lesson, Long> {
    @Query("select distinct new lms.dto.response.LessonResponse(i.id, i.title) from Lesson i order by i.id asc")
    List<LessonResponse> findLessons();
    default Page<LessonResponse> findAllLessons(Pageable pageable){
        List<LessonResponse> lessons = findLessons();
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), lessons.size());
        return new PageImpl<>(lessons.subList(start, end), pageable, lessons.size());
    }
}
