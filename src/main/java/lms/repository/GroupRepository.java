package lms.repository;

import lms.dto.response.GroupResponse;
import lms.entities.Group;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface GroupRepository extends JpaRepository<Group, Long> {

    boolean existsByTitle(String name);

    @Query("select g from Group g where g.title = :title")
    Group findByTitle(String title);

    @Query("""
                select new lms.dto.response.GroupResponse
                (g.id, g.title, g.description, g.image, g.dateOfStart, g.dateOfEnd)
                from Group g where g.trash.id is null 
            """)
    Page<GroupResponse> findAllGroup(Pageable pageable);

    @Query("select g.id from Group g join g.courses c join c.instructors i where i.id = :id")
    List<Long> findAllByInstructorId(Long id);
}
