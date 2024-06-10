package lms.repository;

import lms.dto.response.GroupWithoutPagination;
import jakarta.transaction.Transactional;
import lms.dto.response.GroupResponse;
import lms.entities.Group;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface GroupRepository extends JpaRepository<Group, Long> {

    boolean existsByTitle(String name);

    @Query("select g from Group g where g.title = :title")
    Optional<Group> findByTitle(String title);

    @Query("""
                select new lms.dto.response.GroupResponse
                (g.id, g.title, g.description, g.image, g.dateOfStart, g.dateOfEnd)
                from Group g where g.trash.id is null 
            """)
    Page<GroupResponse> findAllGroup(Pageable pageable);

    @Query("select g.id from Group g join g.courses c join c.instructors i where i.id = :id")
    List<Long> findAllByInstructorId(Long id);

    @Query("select s from Group s where s.id =:groupId")
    Optional<Group> findGroupById(Long groupId);

    @Query("select new lms.dto.response.GroupWithoutPagination(g.id, g.title) from Group g where g.trash is null")
    List<GroupWithoutPagination> findAllGroupsWithoutTrash();

    @Modifying
    @Transactional
    @Query(value = "delete from announcements_groups where groups_id =:groupId",nativeQuery = true)
    void deleteFromAdditionalTable(@Param("groupId") Long groupId);


    @Query("select count (c) from Course  c")
    int getAllGroupsCount();

    @Query("select s from Group s where s.id in (:ids)")
    List<Group> allGroupById(@Param("ids") List<Long> ids);

    @Modifying @Transactional
    @Query("update Group g set g.trash = null where g.trash.id = :id")
    void clearGroupTrash(Long id);
}
