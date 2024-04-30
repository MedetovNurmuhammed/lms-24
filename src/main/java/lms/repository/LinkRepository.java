package lms.repository;

import lms.entities.Link;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LinkRepository extends JpaRepository<Link, Long> {
    @Query("select l from Link l where l.lesson.id = :lessonId order by l.id asc")
    List<Link> findAllLinksByLesson(@Param("lessonId") Long lessonId);

    default Page<Link> findAllLinksByLessonId(Pageable pageable, Long lessonId) {
        List<Link> links = findAllLinksByLesson(lessonId);
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), links.size());
        List<Link> sublist = links.subList(start, end);
        return new PageImpl<>(sublist, pageable, links.size());
    }
}
