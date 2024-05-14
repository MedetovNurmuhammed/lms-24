package lms.repository;

import lms.entities.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface QuestionRepository extends JpaRepository<Question, Long> {
    @Query("select q from Question q join q.options op where q.test.id =:testId order by op.id asc")
    List<Question> findAllByTestId(@Param("testId") Long testId);
@Query("select count(o.isTrue) from Question q join q.options o")
    double countCurrentAnswersByQuestionId(Long id);
}
