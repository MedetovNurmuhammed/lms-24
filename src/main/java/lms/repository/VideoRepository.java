package lms.repository;

import jakarta.transaction.Transactional;
import lms.dto.response.VideoResponse;
import lms.entities.Video;
import lms.exceptions.NotFoundException;
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

    @Query("select count(v) > 0 from Video v where  v.link.title =:title and v.lesson.id = :id ")
    boolean existsNotNullTrashVideo(@Param("id") Long id, @Param("title") String title);

    @Query("select count(v) > 0 from Lesson s join s.videos v where v.link.title = :title and s.id = :id and v.trash.id is null")
    boolean existsTitle(@Param("id") Long id, @Param("title") String title);

    @Modifying @Transactional
    @Query("update Video v set v.trash = null where v.trash.id = :id")
    void clearVideoTrash(Long id);

    @Query("select v from Video v where v.id = ?1")
    Optional<Video> findTrash(Long id);

    default Video findByIdOrThrow(Long id){
        return findTrash(id)
                .orElseThrow(() -> new NotFoundException("Video with id %d not found".formatted(id)));
    }
    @Query("select v from Video v where v.trash.id = :id")
    Video getVideoByTrashId(Long id);
}
