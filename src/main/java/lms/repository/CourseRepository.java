package lms.repository;

import com.amazonaws.services.route53recoveryreadiness.model.CellOutput;
import lms.dto.response.FindAllResponseCourse;
import lms.entities.Course;
import lms.entities.Option;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface CourseRepository extends JpaRepository<Course, Long> {
    boolean existsByTitle(String courseName);

    @Query("""
                select new lms.dto.response.FindAllResponseCourse
                (c.id, c.title, c.description, c.image,c.dateOfStart)
                from Course c
            """)
    Page<FindAllResponseCourse> findAllCourse(Pageable pageable);

}
