package lms.repository;

import lms.dto.response.TrashResponse;
import lms.entities.Trash;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import org.springframework.stereotype.Repository;
import java.time.ZonedDateTime;
import java.util.List;

public interface TrashRepository extends JpaRepository<Trash, Long> {

    List<Trash> findByDateOfDeleteBefore(ZonedDateTime now);

    @Query("SELECT NEW lms.dto.response.TrashResponse(t.id, t.type, t.name, t.dateOfDelete) " +
            "FROM Trash t where (t.type = lms.enums.Type.COURSE or t.type = lms.enums.Type.STUDENT or t.type = lms.enums.Type.INSTRUCTOR or t.type = lms.enums.Type.GROUP) ")
    Page<TrashResponse> findAllTrashes(Pageable pageable);

    @Query("SELECT NEW lms.dto.response.TrashResponse(t.id, t.type, t.name, t.dateOfDelete) " +
            "FROM Trash t where (t.type = lms.enums.Type.VIDEO or" +
            " t.type = lms.enums.Type.PRESENTATION or " +
            "t.type = lms.enums.Type.LINK or " +
            "t.type = lms.enums.Type.TEST or " +
            "t.type = lms.enums.Type.TASK or " +
            "t.type = lms.enums.Type.LESSON  ) ")
    Page<TrashResponse> findAllInstructorTrashes(Pageable pageable);
}
