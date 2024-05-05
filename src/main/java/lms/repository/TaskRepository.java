package lms.repository;

import lms.dto.response.TaskResponse;
import lms.entities.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface TaskRepository extends JpaRepository<Task, Long> {

    @Query("select t from Task t where t.lesson.id=:lessonId ")
    Page<Task> findAll(Long lessonId, Pageable pageable);
}
