package lms.repository;

import lms.dto.response.StudentResponse;
import lms.dto.response.StudentTestResponse;
import lms.dto.response.TestResponseForGetAll;
import lms.entities.Test;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TestRepository extends JpaRepository<Test, Long> {
    @Query("""
            select new lms.dto.response.TestResponseForGetAll(t.id,t.title,t.hour,t.minute) 
            from Test t where t.lesson.id =:lessonId 
            """)
    List<TestResponseForGetAll> findAllTestsByLessonId(Long lessonId);

}
