package lms.repository;

import lms.dto.response.AllGroupResponse;
import lms.entities.Group;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.NoSuchElementException;

public interface GroupRepository extends JpaRepository<Group, Long> {

    boolean existsByTitle(String name);

    @Query("select g from Group g")
    Group findByTitle(String title);

    default Group getById(long id) {
        return findById(id).orElseThrow(() -> new NoSuchElementException("Группа не найдена"));
    }

    @Query("""
                select new lms.dto.response.AllGroupResponse
                (g.id, g.title, g.description, g.image, g.dateOfStart, g.dateOfEnd)
                from Group g
            """)
    Page<AllGroupResponse> findAllGroup(Pageable pageable);

    @Query("select g.title from Group g ")
    List<String> getAllGroupName();
}
