package lms.repository;

import jakarta.transaction.Transactional;
import lms.dto.response.AllInstructorResponse;
import lms.entities.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface InstructorRepository extends JpaRepository<Instructor, Long> {
    @Query("select new lms.dto.response.AllInstructorResponse(s.id,s.user.fullName,s.specialization,s.user.phoneNumber,s.user.email) from Instructor s ")
    Page<AllInstructorResponse> findAllIns(Pageable pageable);

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
