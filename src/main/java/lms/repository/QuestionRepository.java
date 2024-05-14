package lms.repository;

import lms.entities.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface QuestionRepository extends JpaRepository<Question, Long> {
    @Query("delete from Question q where q.id = :questionId")
    void deleteQuestionById(Long questionId);
}
