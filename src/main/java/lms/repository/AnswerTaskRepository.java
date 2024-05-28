package lms.repository;

import lms.dto.response.FilterAnswerOfTaskResponse;
import lms.entities.AnswerTask;
import lms.enums.TaskAnswerStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface AnswerTaskRepository extends JpaRepository<AnswerTask, Long> {

    @Query("select new lms.dto.response.FilterAnswerOfTaskResponse(a.id,a.student.user.fullName) from AnswerTask  a " +
            " where a.task.id = :taskId and a.taskAnswerStatus = :answerStatus")
    List<FilterAnswerOfTaskResponse> filterAnswerTask(Long taskId, TaskAnswerStatus answerStatus);

    @Query("select a from AnswerTask  a where a.task.id = :taskId and a.student.user.email=:email")
    Optional<AnswerTask> findByTaskId(Long taskId, String email);

    @Query("select count(a)>0 from AnswerTask a where a.task.id = :taskId and a.student.id = :studentId")
    Boolean existsByTaskId(Long taskId, Long studentId);
}
