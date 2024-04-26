package lms.repository;

import lms.entities.AnswerTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface AnswerTaskRepository extends JpaRepository<AnswerTask, Long> {
}
