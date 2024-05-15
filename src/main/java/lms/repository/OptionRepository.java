package lms.repository;

import lms.entities.Option;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface OptionRepository extends JpaRepository<Option, Long> {

    @Query("select count(o) from Option o where o.question.id=:questionId  and o.isTrue = true ")
    int  getTrueOptionSize(Long questionId);
}
