package lms.repository;

import lms.entities.Question;
import org.springframework.data.jpa.repository.JpaRepository;
public interface QuestionRepository extends JpaRepository<Question, Long> {
}
