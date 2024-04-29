package lms.repository;

import lms.entities.AnswerTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface AnswerTaskRepository extends JpaRepository<AnswerTask, Long> {
    @Query("select a from AnswerTask a join Link l on a.id = l.answerTask.id where l.id =:linkId")
    AnswerTask findByLinkId(Long linkId);
}
