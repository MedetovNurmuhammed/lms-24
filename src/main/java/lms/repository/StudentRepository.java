package lms.repository;

import lms.dto.response.StudentResponse;
import lms.entities.Student;
import lms.enums.StudyFormat;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


public interface StudentRepository extends JpaRepository<Student, Long> {

    @Query("""
            select  new lms.dto.response.StudentResponse(s.id, s.user.fullName, s.user.phoneNumber, s.group.title, s.studyFormat, s.user.email)
               from Student s
                 where (:groupId is null or s.group.id = :groupId)
                   and ( s.studyFormat in (:studyFormats))
                   and ( s.user.fullName ilike concat('%',:searchTerm,'%') or s.user.phoneNumber ilike concat('%',:searchTerm,'%') or s.user.email
                    ilike concat ('%',:searchTerm,'%') or s.group.title ilike concat('%',:searchTerm,'%'))
            """)
    List<StudentResponse> findAllBySearchTerm(String searchTerm, List<StudyFormat> studyFormats, Long groupId);

    default Page<StudentResponse> searchAll(String searchTerm, List<StudyFormat> studyFormats, Long groupId, Pageable pageable) {
        List<StudentResponse> allBySearchTerm = findAllBySearchTerm(searchTerm, studyFormats, groupId);
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), allBySearchTerm.size());
        return new PageImpl<>(allBySearchTerm.subList(start, end), pageable, allBySearchTerm.size());
    }

}
