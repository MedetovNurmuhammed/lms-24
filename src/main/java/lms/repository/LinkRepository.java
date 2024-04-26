package lms.repository;

import lms.dto.response.LessonResponse;
import lms.dto.response.LinkResponse;
import lms.entities.Link;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LinkRepository extends JpaRepository<Link, Long> {
    @Query("select new lms.dto.response.LinkResponse(l.id, l.url) from Link l where l.lesson.id = :lessonId order by l.id asc")
    List<LinkResponse> findAllLinks(@Param("lessonId") Long lessonId);
    default Page<LinkResponse> findAllLinkByLessonId(Pageable pageable, Long lessonId){
        List<LinkResponse> links = findAllLinks(lessonId);
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), links.size());
        return new PageImpl<>(links.subList(start, end), pageable, links.size());
    };
}
