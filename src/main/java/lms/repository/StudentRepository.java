package lms.repository;

import jakarta.transaction.Transactional;
import lms.dto.response.InstructorsOrStudentsOfCourse;
import lms.dto.response.StudentResponse;
import lms.entities.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import lms.enums.StudyFormat;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student,Long> {


    @Query("select new lms.dto.response.InstructorsOrStudentsOfCourse(s.id, c.title, s.user.fullName, '', s.user.phoneNumber, s.user.email) " +
            "from Student s " +
            "join s.group g " +
            "join g.courses c " +
            "where c.id = :courseId")
    Page <InstructorsOrStudentsOfCourse> getStudentsByCourseId(@Param("courseId") Long courseId,Pageable pageable);

    @Query("""
            select  new lms.dto.response.StudentResponse(s.id, s.user.fullName, s.user.phoneNumber, s.group.title, s.studyFormat, s.user.email,s.user.block)
               from Student s
                 where  (:groupId is null or s.group.id = :groupId)
                   and ( s.studyFormat in (:studyFormats))
                   and ( s.user.fullName ilike concat('%',:searchTerm,'%') or s.user.phoneNumber ilike concat('%',:searchTerm,'%') or s.user.email
                    ilike concat ('%',:searchTerm,'%') or s.group.title ilike concat('%',:searchTerm,'%') or :searchTerm is null ) and s.trash is null
            """)
    Page<StudentResponse> findAllBySearchTerm(String searchTerm, List<StudyFormat> studyFormats, Long groupId,Pageable pageable);

    default Page<StudentResponse> searchAll(String searchTerm, List<StudyFormat> studyFormats, Long groupId, Pageable pageable) {
        List<StudentResponse> allBySearchTerm = findAllBySearchTerm(searchTerm, studyFormats, groupId);
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), allBySearchTerm.size());
        return new PageImpl<>(allBySearchTerm.subList(start, end), pageable, allBySearchTerm.size());
    }

    @Query("select new lms.dto.response.StudentResponse(s.id,s.user.fullName,s.user.phoneNumber,s.group.title,s.studyFormat,s.user.email) from Student s where s.group.id = :groupId and s.trash is null")
    List<StudentResponse> findAllByGroupId(Pageable pageable, Long groupId);



    @Query("select s from Student s where s.user.id =:id ")
    Optional<Student> findByUserId(Long id);

    @Query("select  s from  Student  s where s.user.email=:email")
    Optional<Student> getStudentByEmail(String email);

    @Query("select s from Student s join s.group.courses c where c.id = :courseId and s.trash is null ")
    List<Student> findByCourseId(Long courseId);

    @Query("select s.id from Student s join s.group.courses c where c.id = :id and s.trash is null ")
    List<Long> findStudentIdByCourseId(Long id);

    @Query("select  s.user.fullName " +
            "from Task t join t.lesson.course.groups g join  g.students s " +
            "where t.id = :taskId and s.id not in (:studentIds)")
    List<String> findUserNamesByTask(@Param("studentIds") List<Long> studentIds,
                                     @Param("taskId") Long taskId);
    @Query("select s from Student s where s.user.id =:id")
    Optional<Student> findStudentByUserId(@Param("id") Long id);

}
