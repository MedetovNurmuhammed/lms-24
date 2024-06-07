package lms.repository;

import lms.dto.response.InstructorNamesResponse;
import jakarta.transaction.Transactional;
import lms.dto.response.InstructorsOrStudentsOfCourse;
import lms.dto.response.InstructorResponse;
import lms.entities.Instructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface InstructorRepository extends JpaRepository<Instructor, Long> {

    @Query(" select distinct new lms.dto.response.InstructorsOrStudentsOfCourse" +
            "(i.id, c.title, i.user.fullName, i.specialization, i.user.phoneNumber, i.user.email, i.user.block) " +
            "from Instructor i join i.courses c " +
            "where c.id = :courseId and i.trashes is empty ")
    Page <InstructorsOrStudentsOfCourse> getInstructorsByCourseId(@Param("courseId")Long courseId, Pageable pageable);

    @Query("select distinct new lms.dto.response.InstructorResponse" +
            "(i.id, i.user.fullName, i.specialization, i.user.phoneNumber, i.user.email) " +
            "from Instructor i where  i.trashes is empty order by i.id asc ")
    Page<InstructorResponse> findAllInstructors(Pageable pageable);

    Optional<Instructor> findByUserId(Long id);

    @Query("select i from Instructor i join i.courses c join c.lessons l join l.tasks t where t.id = :taskId")
    List<Instructor> findByAnswerTask(Long taskId);
@Query("select count (i) from Instructor  i")
    int getAllInstructorsCount();



    @Query("select distinct new lms.dto.response.InstructorNamesResponse(i.id, u.fullName) from Instructor i join i.user u order by i.id")
    List<InstructorNamesResponse> AllInstructorName();
    @Query("select s from Instructor s where s.id =:instructorId")
    Optional<Instructor> findInstructorById(Long instructorId);

    @Query("select i from Instructor i where i.user.id = :id")
    Instructor getInstructorByUserID(Long id);
}
