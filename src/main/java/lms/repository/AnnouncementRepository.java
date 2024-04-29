package lms.repository;

import jakarta.transaction.Transactional;
import lms.dto.response.AnnouncementResponse;
import lms.entities.Announcement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface AnnouncementRepository extends JpaRepository<Announcement, Long> {

    @Query("select new lms.dto.response.AnnouncementResponse(a.id,a.announcementContent,a.publishedDate,a.expirationDate,a.isPublished) from Announcement  a  ")
    Page<AnnouncementResponse> findAllAnnouncement(Pageable pageable);

    @Query("select new lms.dto.response.AnnouncementResponse(a.id,a.announcementContent,a.publishedDate,a.expirationDate,a.isPublished) from Announcement  a join a.groups g where g.id =:groupId ")
    Page<AnnouncementResponse> findAllByGroupId(Long groupId, Pageable pageable);

    @Transactional
    @Modifying
    @Query(value = "delete from student_announcements where announcements_key = :announcementId", nativeQuery = true)
    void deleteByAnnouncementIdNative(Long announcementId);

    List<Announcement> findByExpirationDateBefore(LocalDate publishedDate);

}
