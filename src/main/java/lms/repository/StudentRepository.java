package lms.repository;

import lms.dto.response.InstructorsOrStudentsOfCourse;
import lms.dto.response.StudentResponse;
import lms.entities.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import lms.enums.StudyFormat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface StudentRepository extends JpaRepository<Student,Long> {

    @Query("select new lms.dto.response.InstructorsOrStudentsOfCourse(i.id, c.title, i.user.fullName, '', i.user.phoneNumber, i.user.email) from Student i join i.group g join g.courses c where c.id = :courseId and i.trash is null")
    List<InstructorsOrStudentsOfCourse> StudentsByCourseId(Long courseId);

    default Page<InstructorsOrStudentsOfCourse> getStudentsByCourseId(Long courseId, Pageable pageable){
        List<InstructorsOrStudentsOfCourse> allStudents = StudentsByCourseId(courseId);
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), allStudents.size());
        return new PageImpl<>(allStudents.subList(start, end), pageable, allStudents.size());
    }

    @Query("""
            select  new lms.dto.response.StudentResponse(s.id, s.user.fullName, s.user.phoneNumber, s.group.title, s.studyFormat, s.user.email)
               from Student s
                 where  (:groupId is null or s.group.id = :groupId)
                   and ( s.studyFormat in (:studyFormats))
                   and ( s.user.fullName ilike concat('%',:searchTerm,'%') or s.user.phoneNumber ilike concat('%',:searchTerm,'%') or s.user.email
                    ilike concat ('%',:searchTerm,'%') or s.group.title ilike concat('%',:searchTerm,'%') or :searchTerm is null ) and s.trash is null
            """)
    List<StudentResponse> findAllBySearchTerm(String searchTerm, List<StudyFormat> studyFormats, Long groupId);

    default Page<StudentResponse> searchAll(String searchTerm, List<StudyFormat> studyFormats, Long groupId, Pageable pageable) {
        List<StudentResponse> allBySearchTerm = findAllBySearchTerm(searchTerm, studyFormats, groupId);
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), allBySearchTerm.size());
        return new PageImpl<>(allBySearchTerm.subList(start, end), pageable, allBySearchTerm.size());
    }

    @Query("select new lms.dto.response.StudentResponse(s.id,s.user.fullName,s.user.phoneNumber,s.group.title,s.studyFormat,s.user.email) from Student s where s.group.id = :groupId and s.trash is null")
    List<StudentResponse> findAllByGroupId(Pageable pageable, Long groupId);
}
