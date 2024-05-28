package lms.repository;

import jakarta.transaction.Transactional;
import lms.entities.User;
import lms.exceptions.NotFoundException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface UserRepository extends JpaRepository<User,Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    default User getByEmail(String email){
        return findByEmail(email).orElseThrow(() ->
                new NotFoundException("User with: "+email+" not found"));
    }

    @Query("select u from User u where u.uuid =:uuid")
    Optional<User> findByUuid( String uuid);

    @Modifying
    @Query("update Announcement a set a.user.id = null where a.user.id = :userId")
    @Transactional
    void detachFromAnnouncement(Long userId);
}
