package lms.repository;

import lms.entities.Trash;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.ZonedDateTime;
import java.util.List;

@Repository
public interface TrashRepository extends JpaRepository<Trash, Long> {

    List<Trash> findByDateOfDeleteBefore(ZonedDateTime now);

    @Query("select t from Trash t where t.id = ?1")
    Trash findTrashById(Long trashId);


}
