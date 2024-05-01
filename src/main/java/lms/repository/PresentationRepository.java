package lms.repository;

import lms.dto.response.PresentationResponse;
import lms.entities.Presentation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PresentationRepository extends JpaRepository<Presentation, Long> {
    @Query("SELECT new lms.dto.response.PresentationResponse(p.id, p.title, p.description, p.file) " +
            "FROM Presentation p " +
            "WHERE p.id = :lessonId")
    Page<PresentationResponse> findAllPresentationsByLesson(@Param("lessonId")Long lessonId, Pageable pageable);

}
