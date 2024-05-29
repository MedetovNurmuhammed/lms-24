package lms.repository;

import lms.dto.response.CourseResponse;
import lms.entities.Course;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CourseRepository extends JpaRepository<Course, Long> {

    boolean existsByTitle(String courseName);

    @Query("""
                select new lms.dto.response.CourseResponse
                (c.id, c.title, c.description, c.image,c.dateOfStart)
                from Course c where c.trash.id is null 
            """)
    Page<CourseResponse> findAllCourse(Pageable pageable);

    @Query("""
                select new lms.dto.response.CourseResponse
                (c.id, c.title, c.description, c.image,c.dateOfStart)
                from Course c join c.instructors i where i.id =:id and c.trash.id is null 
            """)
    Page<CourseResponse> findByInstructorId(Long id, Pageable pageable);

    @Query("""
                select new lms.dto.response.CourseResponse
                (c.id, c.title, c.description, c.image,c.dateOfStart)
                from Student s join s.group g join g.courses c  where s.id =:id and c.trash.id is null 
            """)
    Page<CourseResponse> findByStudentId(Long id, Pageable pageable);
}

