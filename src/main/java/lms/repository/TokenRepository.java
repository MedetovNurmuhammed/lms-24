package lms.repository;

import jakarta.transaction.Transactional;
import lms.entities.Instructor;
import lms.entities.Notification;
import lms.entities.ResultTask;
import lms.entities.Token;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token, Long> {
    Optional<Token>findTokenByValue(String value);
    Token findByUserId(Long userId);
//    List<Token> findAllByExpired(LocalDateTime localDateTime);

}
