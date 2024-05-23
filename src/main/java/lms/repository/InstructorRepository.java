package lms.repository;

import lms.dto.response.InstructorsOrStudentsOfCourse;
import lms.dto.response.InstructorResponse;
import lms.entities.AnswerTask;
import lms.entities.Instructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface InstructorRepository extends JpaRepository<Instructor, Long> {
    @Query("SELECT DISTINCT new lms.dto.response.InstructorsOrStudentsOfCourse(i.id, c.title, i.user.fullName, i.specialization, i.user.phoneNumber, i.user.email) " +
            "FROM Instructor i JOIN i.courses c " +
            "WHERE c.id = :courseId AND i.trash IS NULL")
    Page <InstructorsOrStudentsOfCourse> getInstructorsByCourseId(@Param("courseId")Long courseId, Pageable pageable);
    @Query("select distinct new lms.dto.response.InstructorResponse(i.id, i.user.fullName, i.specialization, i.user.phoneNumber, i.user.email) from Instructor i where  i.trash is null order by i.id asc ")
    List<InstructorResponse> findAllInstructors();
    default Page<InstructorResponse> findAllInstructorsss( Pageable pageable){
        List<InstructorResponse> instructorsOrStudentsOfCourses = findAllInstructors();
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), instructorsOrStudentsOfCourses.size());
        return new PageImpl<>(instructorsOrStudentsOfCourses.subList(start, end), pageable, instructorsOrStudentsOfCourses.size());
    }

    Optional<Instructor> findByUserId(Long id);

    @Query("select i from Instructor i join i.courses c join c.lessons l join l.tasks t where t.id = :taskId")
    List<Instructor> findByAnswerTask(Long taskId);
}
