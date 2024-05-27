package lms.repository;

import lms.dto.response.VideoResponse;
import lms.entities.Video;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;

public interface VideoRepository extends JpaRepository<Video, Long> {
    @Query("select new lms.dto.response.VideoResponse(v.id, v.link.title,v.description, v.link.url, v.createdAt) " +
            "from Lesson l  join l.videos v where l.id = :lessonId and v.trash is null order by v.createdAt asc")
    Page<VideoResponse> findAllVideo(@Param("lessonId") Long lessonId, Pageable pageable);

@Query("select s from Video s where s.id =:videoId and s.trash is null")
    Optional<Video> findVideoById(@Param("videoId") Long videoId);
}
