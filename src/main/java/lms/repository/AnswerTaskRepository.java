package lms.repository;

import lms.dto.response.FilterAnswerOfTaskResponse;
import lms.entities.AnswerTask;
import lms.enums.TaskAnswerStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface AnswerTaskRepository extends JpaRepository<AnswerTask, Long> {

   @Query("select new lms.dto.response.FilterAnswerOfTaskResponse(a.id,a.student.user.fullName) from AnswerTask  a " +
           " where a.task.id = :taskId and a.taskAnswerStatus = :answerStatus")
   List<FilterAnswerOfTaskResponse> filterAnswerTask(Long taskId, TaskAnswerStatus answerStatus);

   @Query("select a from AnswerTask  a where a.task.id = :taskId and a.student.user.email=:email")
   AnswerTask findByTaskId(Long taskId, String email);
}
