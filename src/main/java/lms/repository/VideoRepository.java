package lms.repository;

import lms.dto.response.VideoResponse;
import lms.entities.Video;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface VideoRepository extends JpaRepository<Video, Long> {
    @Query("select new lms.dto.response.VideoResponse(v.id, v.link.title,v.description, v.link.url, v.createdAt) " +
            "from Lesson l  join l.videos v where l.id = :lessonId and v.trash is null order by v.createdAt asc")
    List<VideoResponse> findAllVideo(@Param("lessonId") Long lessonId);
    default Page<VideoResponse> findAllVideosByLessonId(Long lessonId, Pageable pageable){
        List<VideoResponse> videos = findAllVideo(lessonId);
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), videos.size());
        return new PageImpl<>(videos.subList(start, end), pageable, videos.size());
    }
@Query("select s from Video s where s.id =:videoId and s.trash is null")
    Optional<Video> findVideoById(@Param("videoId") Long videoId);
}
