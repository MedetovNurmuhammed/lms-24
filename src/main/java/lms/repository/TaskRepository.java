package lms.repository;

import lms.entities.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {

    @Query("select t from Task t where t.lesson.id=:lessonId and t.trash.id is null")
    List<Task> findAll(Long lessonId);
}
