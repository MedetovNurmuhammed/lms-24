package lms.repository;

import lms.entities.Link;
import lms.exceptions.NotFoundException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface LinkRepository extends JpaRepository<Link, Long> {

//    @Query("select l from  Lesson l where l.id = :linkId")
    default Link findd(Long linkId) {
        Link link = findById(linkId).orElseThrow(() ->
                new NotFoundException(" invalid linkId !!"));

        return link ;
    }
}
