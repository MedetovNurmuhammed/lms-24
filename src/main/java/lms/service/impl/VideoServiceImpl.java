package lms.service.impl;

import jakarta.transaction.Transactional;
import lms.dto.request.VideoRequest;
import lms.dto.response.SimpleResponse;
import lms.dto.response.VideoResponse;
import lms.entities.Lesson;
import lms.entities.Video;
import lms.entities.Link;
import lms.entities.Trash;
import lms.entities.*;
import lms.enums.Type;
import lms.exceptions.AlreadyExistsException;
import lms.exceptions.BadRequestException;
import lms.exceptions.NotFoundException;
import lms.repository.*;
import lms.service.VideoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.time.ZonedDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Validated
@Slf4j
public class VideoServiceImpl implements VideoService {
    private final VideoRepository videoRepository;
    private final LessonRepository lessonRepository;
    private final LinkRepository linkRepository;
    private final TrashRepository trashRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public SimpleResponse save(VideoRequest videoRequest, Long lessonId) {
        Lesson lesson = lessonRepository.findLessonById(lessonId).orElseThrow(() -> new NotFoundException("урок с id " + lessonId + " не найден"));
        if (lesson.getTrash() == null) {
            existByVideoTitle(videoRequest, lesson);
            Video video = new Video();
            Link link = new Link();
            video.setDescription(videoRequest.description());
            link.setTitle(videoRequest.titleOfVideo());
            link.setVideo(video);
            link.setUrl(videoRequest.linkOfVideo());
            lesson.getVideos().add(video);
            video.setLesson(lesson);
            linkRepository.save(link);
            videoRepository.save(video);
            return SimpleResponse.builder()
                    .httpStatus(HttpStatus.OK)
                    .message("видео с названием " + link.getTitle() + " успешно сохранён")
                    .build();
        } else throw new AlreadyExistsException("Данные уже в корзине");
    }

    private void existByVideoTitle(VideoRequest videoRequest, Lesson lesson) {
        boolean exists = videoRepository.existsTitle(lesson.getId(), videoRequest.titleOfVideo());
        if (exists) {
            throw new AlreadyExistsException("видео с названием " + videoRequest.titleOfVideo() + " уже существует!");
        }
        boolean notNullTrashVideos = videoRepository.existsNotNullTrashVideo(lesson.getId(), videoRequest.titleOfVideo());
        if (notNullTrashVideos)
            throw new AlreadyExistsException("видео с названием " + videoRequest.titleOfVideo() + " уже есть в корзине!");
    }

    @Override
    @Transactional
    public SimpleResponse update(Long videoId, VideoRequest videoRequest) {
        Video video = videoRepository.findVideoById(videoId)
                .orElseThrow(() -> new NotFoundException("Видео с id " + videoId + " не найдено"));
        if (video.getTrash() == null) {
            Lesson lesson = linkRepository.findByVideoId(video.getId());
            existByVideoTitle(videoRequest, lesson);

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
        } else throw new BadRequestException("Урок может быть в корзине!");
    }

    @Override
    public List<VideoResponse> findAllVideoByLessonId(Long lessonId) {
        return videoRepository.findAllVideo(lessonId);
    }

    @Override
    public VideoResponse findById(Long videoId) {
        Video video = videoRepository.findVideoById(videoId)
                .orElseThrow(() -> new NotFoundException("Видео с id " + videoId + " не найдено"));
        if (video.getTrash() == null) {
            return VideoResponse.builder()
                    .id(video.getId())
                    .titleOfVideo(video.getLink().getTitle())
                    .linkOfVideo(video.getLink().getUrl())
                    .description(video.getDescription())
                    .createdAt(video.getCreatedAt())
                    .build();
        } else throw new BadRequestException("Урок может быть в корзине!");
    }

    @Override
    @Transactional
    public SimpleResponse delete(Long videoId) {
        User authUser = userRepository.getByEmail(SecurityContextHolder.getContext().getAuthentication().getName());
        Video video = videoRepository.findVideoById(videoId)
                .orElseThrow(() -> new NotFoundException("Видео с id " + videoId + " не найдено"));
        if (video.getTrash() == null) {
            Trash trash = new Trash();
            trash.setName(video.getDescription());
            trash.setDateOfDelete(ZonedDateTime.now());
            trash.setType(Type.VIDEO);
            trash.setCleanerId(authUser.getId());
            video.setTrash(trash);
            trashRepository.save(trash);
            return SimpleResponse.builder()
                    .httpStatus(HttpStatus.OK)
                    .message("Видео с названием " + video.getLink().getTitle() + " успешно добавлено в карзину")
                    .build();
        }else throw new BadRequestException("Урок может быть в корзине!");
    }
}
