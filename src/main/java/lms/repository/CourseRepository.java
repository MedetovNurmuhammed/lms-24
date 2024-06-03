package lms.repository;

import jakarta.transaction.Transactional;
import lms.dto.response.CourseResponse;
import lms.entities.Course;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CourseRepository extends JpaRepository<Course, Long> {

    boolean existsByTitle(String courseName);

    @Query("""
                select new lms.dto.response.CourseResponse
                (c.id, c.title, c.description, c.image,c.dateOfStart)
                from Course c where c.trash.id is null 
            """)
    Page<CourseResponse> findAllCourse(Pageable pageable);


    @Modifying
    @Transactional
    @Query(value = "DELETE FROM courses_groups WHERE courses_id = :courseId", nativeQuery = true)
    void detachFromExtraTable(@Param("courseId") Long courseId);

    @Modifying
    @Transactional
    @Query(value = "delete from instructors_courses where courses_id =:courseId ;",nativeQuery = true)
    void detachFromCoursesInstructors(@Param("courseId") Long courseId);
    @Modifying
    @Transactional
    @Query(value = "delete from courses_groups where course_id = :courseId", nativeQuery = true)
    void deleteCourseGroups(@Param("courseId") Long courseId);

    @Modifying
    @Transactional
    @Query(value = "delete from instructor_notification_states where course_id = :courseId", nativeQuery = true)
    void deleteInstructorNotificationStates(@Param("courseId") Long courseId);

    @Modifying
    @Transactional
    @Query(value = "delete from instructor_courses where course_id = :courseId", nativeQuery = true)
    void deleteInstructorCourses(@Param("courseId") Long courseId);


    @Transactional
    @Modifying
    @Query(value = "delete from student_notification_states where notification_states_key = :notificationId and student_id = :userId", nativeQuery = true)
    void deleteNotificationFromExtraTableStudent(@Param("notificationId")Long notificationId,@Param("userId") Long userId);

    @Modifying
    @Transactional
    @Query(value = "delete from courses_groups where courses_id = :courseId", nativeQuery = true)
    void deleteCourseAndGroup(Long courseId);


}

