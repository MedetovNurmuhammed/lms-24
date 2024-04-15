package lms.repository;

import lms.entities.AnswerTask;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnswerTaskRepository extends JpaRepository<AnswerTask, Long> {
}
