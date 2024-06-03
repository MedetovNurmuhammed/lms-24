package lms.repository;

import jakarta.transaction.Transactional;
import lms.entities.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    @Modifying
    @Transactional
    @Query("delete from Comment c where c.answerTask.id =:answerTaskId")
    void deleteByAnswerTaskId(@Param("answerTaskId") Long answerTaskId);
}
