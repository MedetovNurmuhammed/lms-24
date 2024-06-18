package lms.repository;

import jakarta.transaction.Transactional;
import lms.entities.Task;
import lms.entities.Video;
import lms.exceptions.NotFoundException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TaskRepository extends JpaRepository<Task, Long> {

    @Query("select t from Task t where t.lesson.id=:lessonId and t.trash.id is null order by t.createdAt desc")
    List<Task> findAll(Long lessonId);

    @Query("select s from Task s where s.id = :taskId")
    Optional<Task> findTaskById(@Param("taskId") Long taskId);

    @Modifying @Transactional
    @Query("update Task t set t.trash = null where t.id = :trashId")
    void clearTaskTrash(Long trashId);

    @Query("select t from Task t where t.trash.id = :trashId")
    Optional<Task> getTaskByTrashId(Long trashId);

}
