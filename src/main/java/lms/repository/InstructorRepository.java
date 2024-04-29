package lms.repository;

import lms.dto.response.InstructorsOrStudentsOfCourse;
import lms.dto.response.InstructorResponse;
import lms.entities.Instructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface InstructorRepository extends JpaRepository<Instructor, Long> {
    @Query("select distinct new lms.dto.response.InstructorsOrStudentsOfCourse(i.id, c.title, i.user.fullName, i.specialization, i.user.phoneNumber, i.user.email) from Instructor i join i.courses c where c.id = :courseId and i.trash is null")
    List<InstructorsOrStudentsOfCourse> getInstructorsByCourseId(Long courseId);
    default Page<InstructorsOrStudentsOfCourse> findAllInstructorsByCourseId(Long courseId, Pageable pageable){
        List<InstructorsOrStudentsOfCourse> instructorsOrStudentsOfCourses = getInstructorsByCourseId(courseId);
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), instructorsOrStudentsOfCourses.size());
        return new PageImpl<>(instructorsOrStudentsOfCourses.subList(start, end), pageable, instructorsOrStudentsOfCourses.size());
    }
    @Query("select distinct new lms.dto.response.InstructorResponse(i.id, i.user.fullName, i.specialization, i.user.phoneNumber, i.user.email) from Instructor i where  i.trash is null order by i.id asc ")
    List<InstructorResponse> findAllInstructors();
    default Page<InstructorResponse> findAllInstructorsss( Pageable pageable){
        List<InstructorResponse> instructorsOrStudentsOfCourses = findAllInstructors();
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), instructorsOrStudentsOfCourses.size());
        return new PageImpl<>(instructorsOrStudentsOfCourses.subList(start, end), pageable, instructorsOrStudentsOfCourses.size());
    }
}
