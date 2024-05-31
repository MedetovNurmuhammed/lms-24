package lms.service.impl;

import jakarta.transaction.Transactional;
import lms.dto.request.LinkRequest;
import lms.dto.response.LinkResponse;
import lms.dto.response.AllLinkResponse;
import lms.dto.response.SimpleResponse;
import lms.entities.Lesson;
import lms.entities.Link;
import lms.entities.Trash;
import lms.enums.Type;
import lms.exceptions.NotFoundException;
import lms.repository.LessonRepository;
import lms.repository.LinkRepository;
import lms.repository.TrashRepository;
import lms.service.LinkService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import java.time.ZonedDateTime;

@Service
@RequiredArgsConstructor
@Validated
@Slf4j
public class LinkServiceImpl implements LinkService {
    private final LinkRepository linkRepository;
    private final LessonRepository lessonRepository;
    private final TrashRepository trashRepository;

    @Override
    public SimpleResponse addLink(LinkRequest linkRequest, Long lessonId) {
        Lesson lesson = lessonRepository.findLessonById(lessonId).orElseThrow(() -> new NotFoundException("Урок c " + lessonId + " не найден"));
        Link link = new Link();
        link.setTitle(linkRequest.title());
        link.setUrl(linkRequest.url());
        link.setLesson(lesson);
        lesson.getLinks().add(link);
        linkRepository.save(link);
        return SimpleResponse.builder()
                .httpStatus(HttpStatus.OK)
                .message("Ccылка с названием " + link.getTitle() + " успешно сохранен!")
                .build();
    }

    @Override
    public AllLinkResponse findAll(int page, int size, Long lessonId) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("id"));
        Page<LinkResponse> allLinks = linkRepository.findAllLinksByLesson(lessonId, pageable);
        return AllLinkResponse.builder()
                .page(allLinks.getNumber() + 1)
                .size(allLinks.getNumberOfElements())
                .linkResponses(allLinks.getContent())
                .build();
    }

    @Override
    public LinkResponse findById(Long linkId) {
        Link link = linkRepository.findLinkById(linkId).orElseThrow(() -> new NotFoundException("ссылка с " + linkId + " не найден"));
        return LinkResponse.builder()
                .id(link.getId())
                .title(link.getTitle())
                .url(link.getUrl())
                .build();
    }

    @Override
    @Transactional
    public SimpleResponse update(LinkRequest linkRequest, Long linkId) {
        Link link = linkRepository.findLinkById(linkId).orElseThrow(() -> new NotFoundException("ссылка с " + linkId + " не найден"));
        link.setTitle(linkRequest.title());
        link.setUrl(linkRequest.url());
        linkRepository.save(link);
        return SimpleResponse.builder()
                .httpStatus(HttpStatus.OK)
                .message("Сcылка с названием " + link.getTitle() + " успешно обновлён!")
                .build();
    }

    @Override
    @Transactional
    public SimpleResponse delete(Long linkId) {
        Link link = linkRepository.findLinkById(linkId).orElseThrow(() -> new NotFoundException("ссылка с " + linkId + " не найден"));
        Trash trash = new Trash();
        trash.setName(link.getTitle());
        trash.setDateOfDelete(ZonedDateTime.now());
        trash.setType(Type.LINK);
        trash.setLink(link);
        link.setTrash(trash);
        trashRepository.save(trash);
        return SimpleResponse.builder()
                .httpStatus(HttpStatus.OK)
                .message("Ссылка с названием " + link.getTitle() + " успешно добавлено в карзину")
                .build();
    }
}
