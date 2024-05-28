package lms.repository;

import jakarta.transaction.Transactional;
import lms.dto.response.PresentationResponse;
import lms.entities.Presentation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PresentationRepository extends JpaRepository<Presentation, Long> {
    @Query("SELECT new lms.dto.response.PresentationResponse(p.id, p.title, p.description, p.file) " +
            "FROM Lesson l join l.presentations p  " +
            "WHERE l.id = :lessonId and p.trash is null")
    List<PresentationResponse> findAllPresentationsByLesson(@Param("lessonId") Long lessonId);

    @Transactional
    @Modifying
    @Query(value = "delete from lessons_presentations where presentations_id = :id", nativeQuery = true)
    void deletePresentation(Long id);
@Query("select s from Presentation s where s.trash.id is not null")
    List<Presentation> findNotNullTrashPresentations();
}
