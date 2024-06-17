package lms.repository;

import jakarta.transaction.Transactional;
import lms.dto.response.PresentationResponse;
import lms.entities.Presentation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PresentationRepository extends JpaRepository<Presentation, Long> {
    @Query("SELECT new lms.dto.response.PresentationResponse(p.id, p.title, p.description, p.file) " +
            "FROM Lesson l join l.presentations p  " +
            "WHERE l.id = :lessonId and p.trash is null order by p.createdAt asc")
    List<PresentationResponse> findAllPresentationsByLesson(@Param("lessonId") Long lessonId);

    @Transactional
    @Modifying
    @Query(value = "delete from lessons_presentations where presentations_id = :id", nativeQuery = true)
    void deletePresentation(Long id);

    @Query("select count(p) > 0 from Presentation p where  p.title =:title and p.lesson.id = :id")
    boolean existsNotNullTrashPresentation(@Param("id") Long id, @Param("title") String title);

    @Query("select count(p) > 0 from Lesson s join s.presentations p where p.title = :title and s.id = :id and p.trash.id is null")
    boolean existsTitle(@Param("id") Long id, @Param("title") String title);
    @Query("select s from Presentation s where s.id =:presentationId")
    Optional<Presentation> findPresentationById(Long presentationId);

    @Modifying @Transactional
    @Query("update Presentation p set p.trash = null where p.trash.id = :id")
    void clearPresentationTrash(Long id);

    @Query("select p from Presentation p where p.trash.id = ?1")
    Optional<Presentation> getByTrashId(Long trashID);
}
