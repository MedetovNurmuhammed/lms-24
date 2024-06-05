package lms.repository;

import jakarta.transaction.Transactional;
import lms.dto.response.TrashResponse;
import lms.entities.Trash;
import lms.exceptions.NotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.ZonedDateTime;
import java.util.List;

public interface TrashRepository extends JpaRepository<Trash, Long> {

    List<Trash> findByDateOfDeleteBefore(ZonedDateTime now);

    @Query("select t from Trash t where t.id = ?1")
    Trash findTrashById(Long trashId);

    @Query("SELECT NEW lms.dto.response.TrashResponse(t.id, t.type, t.name, t.dateOfDelete) " +
            "FROM Trash t  where (t.type = lms.enums.Type.COURSE or t.type = lms.enums.Type.STUDENT or t.type = lms.enums.Type.INSTRUCTOR or t.type = lms.enums.Type.GROUP)" )
    Page<TrashResponse> findAllTrashes(Pageable pageable);


    @Query("SELECT NEW lms.dto.response.TrashResponse(t.id, t.type, t.name, t.dateOfDelete) " +
            "FROM Trash t " +
            "WHERE t.type IN (lms.enums.Type.VIDEO, lms.enums.Type.PRESENTATION, lms.enums.Type.LINK, " +
            "lms.enums.Type.TEST, lms.enums.Type.TASK, lms.enums.Type.LESSON) " +
            "AND t.instructor.id = :currentUserId")
    Page<TrashResponse> findAllInstructorTrashes(Pageable pageable, @Param("currentUserId") Long currentUserId);

    @Modifying
    @Transactional
    @Query("delete from Trash t where t.task.id =:taskId")
    void deleteTrashLessons(Long taskId);

    @Modifying
    @Transactional
    @Query("update Trash t set t.student.id = null where t.student.id = :studentId")
    void deleteTrashById(Long studentId);

    @Query("select new lms.dto.response.TrashResponse(t.id, t.type, t.name, t.dateOfDelete) from Trash t")
    Page<TrashResponse> findAllTrash(Pageable pageable);
    @Query("""
            select new lms.dto.response.TrashResponse(t.id, t.type, t.name, t.dateOfDelete) 
            from Trash t
            where t.instructor.id = :id
            """ )
    Page<TrashResponse> findAllTrashByInstructorId(Long id, Pageable pageRequest);

    default Trash findByIdOrThrow(Long id){
        return findById(id)
                .orElseThrow(() -> new NotFoundException("Trash with id %d not found".formatted(id)));
    }
}
