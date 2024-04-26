package lms.service.impl;

import lms.dto.request.LinkRequest;
import lms.dto.response.*;
import lms.entities.Lesson;
import lms.entities.Link;
import lms.exceptions.NotFoundException;
import lms.repository.LessonRepository;
import lms.repository.LinkRepository;
import lms.service.LinkService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Service
@RequiredArgsConstructor
@Validated
@Slf4j
public class LinkServiceImpl implements LinkService {
    private final LinkRepository linkRepository;
    private final LessonRepository lessonRepository;
    @Override
    public SimpleResponse addLink(LinkRequest linkRequest, Long lessonId) {
        Lesson lesson = lessonRepository.findById(lessonId).orElseThrow(() -> new NotFoundException("Урок c " + lessonId + " не найден"));
        Link link = new Link();
        link.setTitle(linkRequest.title());
        link.setUrl(linkRequest.url());
        link.setLesson(lesson);
        lesson.getLinks().add(link);
        linkRepository.save(link);
        return  SimpleResponse.builder()
                .httpStatus(HttpStatus.OK)
                .message("Успешно ccылка с названием "+ link.getTitle()+" сохранен!")
                .build();
    }

    @Override
    public AllLinkResponse findAll(int page, int size, Long lessonId) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<LinkResponse> allLinks = linkRepository.findAllLinkByLessonId(pageable, lessonId);

        return AllLinkResponse.builder()
                .page(allLinks.getNumber() + 1)
                .size(allLinks.getSize())
                .linkResponses(allLinks.getContent())
                .build();
    }

    @Override
    public LinkResponse findById(Long linkId) {
        return null;
    }

    @Override
    public SimpleResponse update(LinkRequest linkRequest, Long linkId) {
        return null;
    }

    @Override
    public SimpleResponse delete(Long lessonId) {
        return null;
    }
}
