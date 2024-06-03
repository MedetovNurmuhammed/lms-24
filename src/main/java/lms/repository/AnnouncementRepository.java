package lms.repository;

import jakarta.transaction.Transactional;
import lms.entities.Announcement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import java.time.LocalDate;
import java.util.List;

public interface AnnouncementRepository extends JpaRepository<Announcement, Long> {

    @Query("select a from Announcement a join a.groups g where g.id =:groupId ")
    Page<Announcement> findAllByGroupId(Long groupId, Pageable pageable);

    @Query("select a from Announcement a join a.groups g where g.id = :groupId")
    List<Announcement> findAllByGroupId(Long groupId);

    @Transactional
    @Modifying
    @Query(value = "delete from student_announcements where announcements_key = :announcementId", nativeQuery = true)
    void deleteByAnnouncementIdNative(Long announcementId);

    List<Announcement> findByExpirationDateBefore(LocalDate publishedDate);

    @Query("select a from Announcement a join a.groups g where g.id in(:ids)")
    Page<Announcement> findAllInstructorAnnouncement(List<Long> ids, Pageable pageable);

    @Modifying
    @Transactional
    @Query(value = "delete from announcements where user_id = :id", nativeQuery = true)
    List<Announcement> deleteByUserId(Long id);
}
