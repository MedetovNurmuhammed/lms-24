package lms.repository;

import lms.entities.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface GroupRepository extends JpaRepository<Group,Long> {
    @Query("select g from Group g where g.title = :groupName")
    Group findByName(String groupName);
}
