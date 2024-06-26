package lms.repository;

import jakarta.transaction.Transactional;
import lms.dto.response.LinkResponse;
import lms.entities.Lesson;
import lms.entities.Link;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface LinkRepository extends JpaRepository<Link, Long> {
    @Query("select new lms.dto.response.LinkResponse(l.id, l.title, l.url) from Link l where l.lesson.id = :lessonId")
    Page<LinkResponse> findAllLinksByLesson(@Param("lessonId")Long lessonId, Pageable pageable);

    @Query("select s from Lesson s join s.videos v where v.id =:id")
    Lesson findByVideoId(@Param("id") Long id);

    @Query("select s from Link s where s.id =:linkId")
    Optional<Link> findLinkById(Long linkId);

    @Modifying @Transactional
    @Query("update Link l set l.trash = null where l.trash.id = :id")
    void clearLinkTrash(Long id);

    @Query("select l from Link l where l.trash.id = :trashId")
    Optional<Link> getLinkByTrashId(Long trashId);
}
