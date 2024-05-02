package lms.repository;

import lms.dto.response.NotificationResponse;
import lms.entities.User;
import lms.exceptions.NotFoundException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    default User getByEmail(String email){
        return findByEmail(email).orElseThrow(() ->
                new NotFoundException("User with: "+email+" not found"));
    }

    @Query("select u from User u where u.uuid =:uuid")
    Optional<User> findByUuid( String uuid);

    @Query("select n from Notification  n where n.instructor.user.id = :id or n.student.user.id = :id")
    List<NotificationResponse> findAllByUserId(Long userId);
}
