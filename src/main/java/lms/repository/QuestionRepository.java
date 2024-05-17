package lms.repository;

import lms.dto.response.QuestionResponse;
import lms.entities.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface QuestionRepository extends JpaRepository<Question, Long> {
    @Query("delete from Question q where q.id = :questionId")
    void deleteQuestionById(Long questionId);
}
