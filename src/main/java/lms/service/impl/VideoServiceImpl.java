package lms.service.impl;

import jakarta.transaction.Transactional;
import lms.dto.request.VideoRequest;
import lms.dto.response.AllVideoResponse;
import lms.dto.response.SimpleResponse;
import lms.dto.response.VideoResponse;
import lms.entities.Lesson;
import lms.entities.Link;
import lms.entities.Trash;
import lms.entities.Video;
import lms.exceptions.BadRequestException;
import lms.exceptions.NotFoundException;
import lms.repository.LessonRepository;
import lms.repository.LinkRepository;
import lms.repository.VideoRepository;
import lms.service.VideoService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDate;
import java.time.ZonedDateTime;

@Service
@Validated
@RequiredArgsConstructor
public class VideoServiceImpl implements VideoService {
    private final VideoRepository videoRepository;
    private final LessonRepository lessonRepository;
    private final LinkRepository linkRepository;

    @Override
    @Transactional
    public SimpleResponse save(VideoRequest videoRequest, Long lessonId) {
        Lesson lesson = lessonRepository.findById(lessonId).orElseThrow(() -> new NotFoundException("урок с id " + lessonId + " не найден"));
        Video video = new Video();
        lesson.getVideos().add(video);
        Link link = new Link();
        video.setDescription(videoRequest.description());
        link.setTitle(videoRequest.titleOfVideo());
        link.setVideo(video);
        link.setUrl(videoRequest.linkOfVideo());
        linkRepository.save(link);
        if (videoRequest.createdAt().isBefore(LocalDate.now())) {
            throw new BadRequestException("Дата создания не должна быть раньше текущей даты");
        }
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
        video.setDescription(videoRequest.description());
        video.setCreatedAt(videoRequest.createdAt());
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
    public AllVideoResponse findAllVideoByLessonId(int size, int page, Long lessonId) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<VideoResponse> allVideo = videoRepository.findAllVideosByLessonId(lessonId, pageable);
        return AllVideoResponse.builder()
                .page(allVideo.getNumber() + 1)
                .size(allVideo.getSize())
                .videoResponses(allVideo.getContent())
                .build();
    }


    @Override
    public VideoResponse findById(Long videoId) {
        Video video = videoRepository.findById(videoId)
                .orElseThrow(() -> new NotFoundException("Видео с id " + videoId + " не найдено"));
        return VideoResponse.builder()
                .titleOfVideo(video.getLink().getTitle())
                .linkOfVideo(video.getLink().getUrl())
                .description(video.getDescription())
                .createdAt(video.getCreatedAt())
                .build();
    }

    @Override
    public SimpleResponse delete(Long videoId) {
        Video video = videoRepository.findById(videoId)
                .orElseThrow(() -> new NotFoundException("Видео с id " + videoId + " не найдено"));
        Trash trash = new Trash();
        trash.setName(video.getDescription());
        trash.setDateOfDelete(ZonedDateTime.now());
        return SimpleResponse.builder()
                .httpStatus(HttpStatus.OK)
                .message("Видео с названием " + video.getLink().getTitle() + " успешно обновлено")
                .build();
    }
}
