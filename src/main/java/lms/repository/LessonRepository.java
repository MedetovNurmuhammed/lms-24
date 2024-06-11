package lms.repository;

import jakarta.transaction.Transactional;
import lms.dto.response.LessonResponse;
import lms.entities.Lesson;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LessonRepository extends JpaRepository<Lesson, Long> {
    @Query("select new lms.dto.response.LessonResponse(i.id, i.title, i.createdAt) from Lesson i where i.course.id = :courseId and i.trash is null order by i.createdAt asc")
    Page<LessonResponse> findAllLessons(@Param("courseId") Long courseId,Pageable pageable);

@Query("select s from Lesson s join s.presentations p where p.id =:id")
    Lesson findLessonByPresentationId(@Param("id") Long id);

    Optional<Lesson> findLessonById(Long lessonId);
    @Modifying
    @Transactional
    @Query(value = "DELETE FROM lessons_presentations WHERE presentations_id = :id", nativeQuery = true)
    void deleteFromAdditionalTable(@Param("id") Long id);

    @Modifying @Transactional
    @Query("update Lesson l set l.trash = null where l.trash.id = :id")
    void clearLessonTrash(Long id);
}

