package lms.repository;

import lms.dto.response.TrashResponse;
import lms.entities.Trash;
import lms.exceptions.NotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface TrashRepository extends JpaRepository<Trash, Long> {

    @Query("select new lms.dto.response.TrashResponse(t.id, t.type, t.name, t.dateOfDelete) from Trash t")
    Page<TrashResponse> findAllTrash(Pageable pageable);
    @Query("""
            select new lms.dto.response.TrashResponse(t.id, t.type, t.name, t.dateOfDelete) 
            from Trash t
            where t.cleanerId = :id
            """ )
    Page<TrashResponse> findAllTrashByAuthId(Long id, Pageable pageRequest);

    @Query("select t from Trash t where t.id = ?1")
    Optional<Trash> findTrash(Long id);
    default Trash findByIdOrThrow(Long id){
        return findTrash(id)
                .orElseThrow(() -> new NotFoundException("Trash with id %d not found".formatted(id)));
    }
}
