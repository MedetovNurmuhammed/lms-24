package lms.repository;

import jakarta.transaction.Transactional;
import lms.dto.response.InstructorResponse;
import lms.entities.Instructor;
import lms.entities.Task;
import lms.entities.ResultTask;
import lms.entities.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface InstructorRepository extends JpaRepository<Instructor, Long> {
    @Query("select distinct new lms.dto.response.InstructorResponse(i.id, i.user.fullName, i.specialization, i.user.phoneNumber, i.user.email) from Instructor i order by i.id asc")
    List<InstructorResponse> findAllInstructors();
    default Page<InstructorResponse> findAllInstructorsss( Pageable pageable){
        List<InstructorResponse> instructorsOrStudentsOfCourses = findAllInstructors();
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), instructorsOrStudentsOfCourses.size());
        return new PageImpl<>(instructorsOrStudentsOfCourses.subList(start, end), pageable, instructorsOrStudentsOfCourses.size());
    }
    @Transactional
    @Query("select n from Notification n where n.instructor.id = :instructorId")
    Notification findNotificationByInstructorId(Long instructorId);

    @Transactional
    @Query("select rt from ResultTask rt where rt.instructor.id = :instructorId")
    ResultTask findResultTaskByInstructorId(Long instructorId);

    @Transactional
    @Query("select t from Task t where t.instructor.id = :instructorId")
    Task findTaskByInstructorId(Long instructorId);
}

