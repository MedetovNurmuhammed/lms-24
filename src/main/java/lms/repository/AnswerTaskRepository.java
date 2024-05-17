package lms.repository;

import lms.dto.response.FilterAnswerOfTaskResponse;
import lms.entities.AnswerTask;
import lms.enums.TaskAnswerStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface AnswerTaskRepository extends JpaRepository<AnswerTask, Long> {

   @Query("select new lms.dto.response.FilterAnswerOfTaskResponse(a.id,a.student.user.fullName) from AnswerTask  a " +
           " where a.task.id = :taskId and a.taskAnswerStatus = :answerStatus")
   List<FilterAnswerOfTaskResponse> filterAnswerTask(Long taskId, TaskAnswerStatus answerStatus);

   @Query("select a from AnswerTask  a where a.task.id = :taskId and a.student.user.email=:email")
   Optional<AnswerTask> findByTaskId(Long taskId, String email);

    @Query("select a from AnswerTask a join Link l on a.id = l.answerTask.id where l.id =:linkId")
    AnswerTask findByLinkId(Long linkId);
}
