package lms.repository;

import lms.entities.Trash;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.ZonedDateTime;
import java.util.List;

public interface TrashRepository extends JpaRepository<Trash, Long> {

    List<Trash> findByDateOfDeleteBefore(ZonedDateTime now);
}
