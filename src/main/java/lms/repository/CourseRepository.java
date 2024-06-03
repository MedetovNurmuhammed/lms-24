package lms.repository;

import lms.dto.response.CourseResponse;
import lms.entities.Course;
import lms.entities.Lesson;
import lms.entities.Student;
import lms.entities.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

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
    @Query("SELECT COUNT(c) FROM Course c")
    int getAllCourseCount();
    @Query("select s from Course s where s.id =:courseId")
    Optional<Course> findCourseById(Long courseId);
    @Query("SELECT s FROM Student s JOIN s.group g JOIN g.courses c WHERE c.id = :courseId")
    List<Student> findStudentsByCourseId(@Param("courseId") Long courseId);
    @Query("select l from Lesson l where l.course.id= :courseId")
    List<Lesson> findAllLessonsByCourseId(Long courseId);
    @Query("SELECT t FROM Task t WHERE t.lesson.course.id = :courseId")
    List<Task> findAllTasksByCourseId(Long courseId);
    @Query("SELECT s FROM Student s " +
            "JOIN s.group g " +
            "JOIN g.courses c " +
            "WHERE c.id = :courseId")
    List<Student> findAllStudentsByCourseId(@Param("courseId") Long courseId);
}

