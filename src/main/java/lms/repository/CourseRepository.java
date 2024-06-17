package lms.repository;

import jakarta.transaction.Transactional;
import lms.dto.response.CourseResponse;
import lms.entities.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
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

    @Modifying @Transactional
    @Query("update Course c set c.trash = null where c.trash.id = :id")
    void clearCourseTrash(Long id);

    @Query("select c from Course c where c.trash.id = :trashId")
    Optional<Course> getByTrashId(Long trashId);

    @Modifying @Transactional
    @Query(value = "delete from instructors_courses ic where ic.courses_id = :courseId ", nativeQuery = true)
    void clearInstructorsByCourseId(Long courseId);

    @Modifying @Transactional
    @Query(value = "delete from courses_groups cg where cg.courses_id = :courseId ", nativeQuery = true)
    void clearGroupsByCourseId(Long courseId);

    @Query(value = """
            select c.* from courses c
            join courses_groups cg
            on c.id = cg.courses_id
            where cg.groups_id = :groupId
            """, nativeQuery = true)
    List<Course> getByGroupsContains(Long groupId);
}

