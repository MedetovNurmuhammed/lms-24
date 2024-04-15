package lms.repository;

import lms.entities.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


public interface StudentRepository extends JpaRepository<Student,Long> {

    @Query("select s from Student s where s.user.block = false")
    Page<Student> findAllStudUnblock(Pageable pageable);

    @Query("select s from Student s where s.user.block = true")
    Page<Student> findAllBlockStud(Pageable pageable);

    @Query("select s from Student s where s.studyFormat = :studyFormat")
    Page<Student> findAllStudByStudyFormat(String studyFormat, Pageable pageable);
}
