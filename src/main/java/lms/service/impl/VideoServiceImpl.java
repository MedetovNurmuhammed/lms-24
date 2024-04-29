package lms.service.impl;

import jakarta.transaction.Transactional;
import lms.dto.response.SimpleResponse;
import lms.entities.Trash;
import lms.entities.Video;
import lms.enums.Type;
import lms.exceptions.NotFoundException;
import lms.repository.TrashRepository;
import lms.repository.VideoRepository;
import lms.service.VideoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import java.time.ZonedDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class VideoServiceImpl implements VideoService {
    private final VideoRepository videoRepository;
    private final TrashRepository trashRepository;

    @Override
    @Transactional
    public SimpleResponse delete(Long studId) {
        Video video = videoRepository.findById(studId).
                orElseThrow(() -> new NotFoundException("Видеоурок не найден!!!"));
        Trash trash = new Trash();
        trash.setName(video.getDescription());
        trash.setType(Type.VIDEO);
        trash.setDateOfDelete(ZonedDateTime.now());
        trash.setVideo(video);
        video.setTrash(trash);
        trashRepository.save(trash);
        return SimpleResponse.builder()
                .httpStatus(HttpStatus.OK)
                .message("Успешно добавлено в корзину!")
                .build();
    }
}
