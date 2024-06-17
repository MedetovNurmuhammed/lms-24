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

    @Transactional
    @Modifying
    @Query(value = "delete from student_announcements where announcement_id = :announcementId", nativeQuery = true)
    void deleteByAnnouncementIdNative(Long announcementId);

    List<Announcement> findByExpirationDateBefore(LocalDate publishedDate);

    @Query("select a from Announcement a join a.groups g where g.id in(:ids)")
    Page<Announcement> findAllInstructorAnnouncement(List<Long> ids, Pageable pageable);

    @Query(value = """
            select a.* from announcements a 
            join announcements_groups ag
            on a.id = ag.announcement_id
            where ag.groups_id = :groupId
            """, nativeQuery = true)
    List<Announcement> getByGroupsContains(Long groupId);
}
