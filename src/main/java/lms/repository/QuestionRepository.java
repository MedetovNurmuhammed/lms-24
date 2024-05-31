package lms.repository;

import lms.entities.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface QuestionRepository extends JpaRepository<Question, Long> {
    @Query("select s from Question s where s.id =:id")
    Optional<Question> findQuestionById(Long id);
}
