package lms.repository;

import lms.dto.response.LinkResponse;
import lms.entities.Lesson;
import lms.entities.Link;
import lms.exceptions.NotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface LinkRepository extends JpaRepository<Link, Long> {

    default Link findd(Long linkId) {
        Link link = findById(linkId).orElseThrow(() ->
                new NotFoundException(" invalid linkId !!"));
        return link;
    }

    @Query("select new lms.dto.response.LinkResponse(l.id, l.title, l.url) from Link l where l.lesson.id = :lessonId")
    Page<LinkResponse> findAllLinksByLesson(@Param("lessonId")Long lessonId, Pageable pageable);

    @Query("select s from Lesson s join s.videos v where v.id =:id")
    Lesson findByVideoId(@Param("id") Long id);

    @Query("select s from Link s where s.id =:linkId")
    Optional<Link> findLinkById(Long linkId);
}
