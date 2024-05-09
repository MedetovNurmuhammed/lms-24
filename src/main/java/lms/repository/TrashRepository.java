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

    @Query("select  new lms.dto.response.TrashResponse (t.id, t.type, t.name, t.dateOfDelete) " +
            "from Trash t")
    Page<TrashResponse> findAllTrashes(Pageable pageable);
}
