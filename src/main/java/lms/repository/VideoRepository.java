package lms.repository;

import jakarta.transaction.Transactional;
import lms.dto.response.VideoResponse;
import lms.entities.Video;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface VideoRepository extends JpaRepository<Video, Long> {
    @Query("select new lms.dto.response.VideoResponse(v.id, v.link.title,v.description, v.link.url, v.createdAt) " +
            "from Lesson l  join l.videos v where l.id = :lessonId and v.trash is null order by v.createdAt asc")
    List<VideoResponse> findAllVideo(@Param("lessonId") Long lessonId);

    @Query("select s from Video s where s.id =:videoId and s.trash is null")
    Optional<Video> findVideoById(@Param("videoId") Long videoId);

    @Query("select count(t) > 0 from Trash t where  t.video.link.title =:title and t.video.lesson.id = :id and t.type = 'VIDEO' ")
    boolean existsNotNullTrashVideo(@Param("id") Long id, @Param("title") String title);

    @Query("select count(v) > 0 from Lesson s join s.videos v where v.link.title = :title and s.id = :id and v.trash.id is null")
    boolean existsTitle(@Param("id") Long id, @Param("title") String title);

//    default Page<VideoResponse> findAllVideosByLessonId(Long lessonId, Pageable pageable) {
//        List<VideoResponse> videos = findAllVideo(lessonId);
//        int start = (int) pageable.getOffset();
//        int end = Math.min((start + pageable.getPageSize()), videos.size());
//        return new PageImpl<>(videos.subList(start, end), pageable, videos.size());
//    }

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM lessons_videos WHERE videos_id = :videoId", nativeQuery = true)
    void deleteFromAdditionalTable(@Param("videoId") Long videoId);
}
