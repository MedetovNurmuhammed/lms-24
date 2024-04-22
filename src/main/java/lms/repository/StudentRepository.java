package lms.repository;

import lms.dto.response.InstructorsOrStudentsOfCourse;
import lms.entities.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface StudentRepository extends JpaRepository<Student,Long> {


    @Query("select new lms.dto.response.InstructorsOrStudentsOfCourse(i.id, c.title, i.user.fullName, '', i.user.phoneNumber, i.user.email) from Student i join i.group g join g.courses c where c.id = :courseId")
    List<InstructorsOrStudentsOfCourse> StudentsByCourseId(Long courseId);

    default Page<InstructorsOrStudentsOfCourse> getStudentsByCourseId(Long courseId, Pageable pageable){
        List<InstructorsOrStudentsOfCourse> allStudents = StudentsByCourseId(courseId);
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), allStudents.size());
        return new PageImpl<>(allStudents.subList(start, end), pageable, allStudents.size());
    }

}
