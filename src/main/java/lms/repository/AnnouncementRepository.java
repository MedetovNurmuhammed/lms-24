package lms.repository;

import lms.dto.response.AnnouncementRequest;
import lms.dto.response.AnnouncementResponse;
import lms.entities.Announcement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


public interface AnnouncementRepository extends JpaRepository<Announcement, Long> {

    default Announcement getById(long id) {
        return findById(id).orElseThrow(()-> new IllegalArgumentException("No announcement found with id: " + id));
    }

    @Query("select new lms.dto.response.AnnouncementResponse(a.id,a.announcementContent,a.publishedDate,a.expirationDate,a.isPublished) from Announcement  a join a.groups g where g.id =:groupId ")
    Page<AnnouncementResponse> findAllByGroupId(long groupId, Pageable pageable);

}
