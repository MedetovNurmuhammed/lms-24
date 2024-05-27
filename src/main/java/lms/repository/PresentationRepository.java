package lms.repository;

import jakarta.transaction.Transactional;
import lms.dto.response.PresentationResponse;
import lms.dto.response.VideoResponse;
import lms.entities.Presentation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
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
    default Page<PresentationResponse> findAllPresentations(Long lessonId, Pageable pageable){
        List<PresentationResponse> presentations = findAllPresentationsByLesson(lessonId);
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), presentations.size());
        return new PageImpl<>(presentations.subList(start, end), pageable, presentations.size());
    }
    @Transactional
    @Modifying
    @Query(value = "delete from lessons_presentations where presentations_id = :id", nativeQuery = true)
    void deletePresentation(Long id);
}
