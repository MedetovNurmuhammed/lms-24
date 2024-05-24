package lms.repository;

import lms.dto.response.InstructorsOrStudentsOfCourse;
import lms.dto.response.InstructorResponse;
import lms.entities.Instructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface InstructorRepository extends JpaRepository<Instructor, Long> {
    @Query("select distinct new lms.dto.response.InstructorsOrStudentsOfCourse" +
            "(i.id, c.title, i.user.fullName, i.specialization, i.user.phoneNumber, i.user.email) " +
            "from Instructor i join i.courses c " +
            "where c.id = :courseId and i.trash.id is null ")
    Page <InstructorsOrStudentsOfCourse> getInstructorsByCourseId(@Param("courseId")Long courseId, Pageable pageable);

    @Query("select distinct new lms.dto.response.InstructorResponse" +
            "(i.id, i.user.fullName, i.specialization, i.user.phoneNumber, i.user.email) " +
            "from Instructor i where  i.trash.id is null order by i.id asc ")
    Page<InstructorResponse> findAllInstructors(Pageable pageable);

    Optional<Instructor> findByUserId(Long id);

    @Query("select i from Instructor i join i.courses c join c.lessons l join l.tasks t where t.id = :taskId")
    List<Instructor> findByAnswerTask(Long taskId);
}
