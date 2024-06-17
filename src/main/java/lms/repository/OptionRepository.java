package lms.repository;

import jakarta.transaction.Transactional;
import lms.entities.Option;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface OptionRepository extends JpaRepository<Option, Long> {

    @Query("select count(o) from Option o where o.question.id=:questionId  and o.isTrue = true ")
    int  getTrueOptionSize(Long questionId);

    @Modifying
    @Transactional
    @Query(value ="delete from result_tests_options r where r.options_id = :optionId  ",nativeQuery = true)
    void deleteOptionById(@Param("optionId") Long optionId);
    @Query("select s from Option s where s.id =:id")
    Optional<Option> findOptionById(Long id);

}
