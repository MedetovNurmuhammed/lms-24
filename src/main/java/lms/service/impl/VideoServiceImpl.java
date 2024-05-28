package lms.service.impl;

import jakarta.transaction.Transactional;
import lms.dto.request.VideoRequest;
import lms.dto.response.SimpleResponse;
import lms.dto.response.VideoResponse;
import lms.entities.Lesson;
import lms.entities.Video;
import lms.entities.Link;
import lms.entities.Trash;
import lms.enums.Type;
import lms.exceptions.AlreadyExistsException;
import lms.exceptions.NotFoundException;
import lms.repository.LessonRepository;
import lms.repository.LinkRepository;
import lms.repository.TrashRepository;
import lms.repository.VideoRepository;
import lms.service.VideoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import java.time.ZonedDateTime;
import java.util.List;

@Service
@Validated
@RequiredArgsConstructor
public class VideoServiceImpl implements VideoService {
    private final VideoRepository videoRepository;
    private final LessonRepository lessonRepository;
    private final LinkRepository linkRepository;
    private final TrashRepository trashRepository;

    @Override
    @Transactional
    public SimpleResponse save(VideoRequest videoRequest, Long lessonId) {
        Lesson lesson = lessonRepository.findById(lessonId).orElseThrow(() -> new NotFoundException("урок с id " + lessonId + " не найден"));
        List<VideoResponse> allVideo = videoRepository.findAllVideo(lesson.getId());
        for (VideoResponse videoResponse : allVideo) {
            if (videoRequest.titleOfVideo().equals(videoResponse.titleOfVideo())) {
                throw new AlreadyExistsException("Видео с названием " + videoRequest.titleOfVideo() + " уже существует!");

            }
        }
        List<Video> notNullTrashVideo = videoRepository.findNotNullTrashVideos(lesson.getId());
        for (Video video : notNullTrashVideo) {
            if (videoRequest.titleOfVideo().equals(video.getLink().getTitle())) {
                throw new AlreadyExistsException("Видео с названием " + videoRequest.titleOfVideo() + " уже есть в корзине!");
            }
        }
        Video video = new Video();
        Link link = new Link();
        video.setDescription(videoRequest.description());
        link.setTitle(videoRequest.titleOfVideo());
        link.setVideo(video);
        link.setUrl(videoRequest.linkOfVideo());
        lesson.getVideos().add(video);
        linkRepository.save(link);
        videoRepository.save(video);
        return SimpleResponse.builder()
                .httpStatus(HttpStatus.OK)
                .message("видео с названием " + link.getTitle() + " успешно сохранён")
                .build();
    }

    @Override
    @Transactional
    public SimpleResponse update(Long videoId, VideoRequest videoRequest) {
        Video video = videoRepository.findById(videoId)
                .orElseThrow(() -> new NotFoundException("Видео с id " + videoId + " не найдено"));
        Lesson lesson = linkRepository.findByVideoId(video.getId());
        List<VideoResponse> allVideo = videoRepository.findAllVideo(lesson.getId());
        for (VideoResponse videoResponse : allVideo) {
            if (videoRequest.titleOfVideo().equals(videoResponse.titleOfVideo())) {
                throw new AlreadyExistsException("Видео с названием " + videoRequest.titleOfVideo() + " уже существует!");
            }
        }
        List<Video> notNullTrashVideo = videoRepository.findNotNullTrashVideos(lesson.getId());
        for (Video video1 : notNullTrashVideo) {
            if (videoRequest.titleOfVideo().equals(video1.getLink().getTitle())) {
                throw new AlreadyExistsException("Видео с названием " + videoRequest.titleOfVideo() + " уже есть в корзине!");
            }
        }
        video.setDescription(videoRequest.description());
        Link link = video.getLink();
        link.setUrl(videoRequest.linkOfVideo());
        link.setTitle(videoRequest.titleOfVideo());
        linkRepository.save(link);
        videoRepository.save(video);
        return SimpleResponse.builder()
                .httpStatus(HttpStatus.OK)
                .message("Видео с названием " + link.getTitle() + " успешно обновлено")
                .build();
    }

    @Override
    public List<VideoResponse> findAllVideoByLessonId(Long lessonId) {
        return videoRepository.findAllVideo(lessonId);
    }

    @Override
    public VideoResponse findById(Long videoId) {
        Video video = videoRepository.findVideoById(videoId)
                .orElseThrow(() -> new NotFoundException("Видео с id " + videoId + " не найдено"));
        return VideoResponse.builder()
                .id(video.getId())
                .titleOfVideo(video.getLink().getTitle())
                .linkOfVideo(video.getLink().getUrl())
                .description(video.getDescription())
                .createdAt(video.getCreatedAt())
                .build();
    }

    @Override
    @Transactional
    public SimpleResponse delete(Long videoId) {
        Video video = videoRepository.findById(videoId)
                .orElseThrow(() -> new NotFoundException("Видео с id " + videoId + " не найдено"));
        Trash trash = new Trash();
        trash.setName(video.getDescription());
        trash.setDateOfDelete(ZonedDateTime.now());
        trash.setType(Type.VIDEO);
        trash.setVideo(video);
        video.setTrash(trash);
        trash.setLink(video.getLink());
        video.getLink().setTrash(trash);
        trashRepository.save(trash);
        return SimpleResponse.builder()
                .httpStatus(HttpStatus.OK)
                .message("Видео с названием " + video.getLink().getTitle() + " успешно добавлено в карзину")
                .build();
    }
}
