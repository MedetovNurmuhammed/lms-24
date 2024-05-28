package lms.service.impl;

import jakarta.transaction.Transactional;
import lms.dto.request.EditPresentationRequest;
import lms.dto.request.PresentationRequest;
import lms.dto.response.SimpleResponse;
import lms.dto.response.PresentationResponse;
import lms.entities.Lesson;
import lms.entities.Presentation;
import lms.entities.Trash;
import lms.enums.Type;
import lms.exceptions.AlreadyExistsException;
import lms.exceptions.NotFoundException;
import lms.repository.LessonRepository;
import lms.repository.PresentationRepository;
import lms.repository.TrashRepository;
import lms.service.PresentationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import java.time.ZonedDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Validated
@Slf4j
public class PresentationServiceImpl implements PresentationService {
    private final LessonRepository lessonRepository;
    private final PresentationRepository presentationRepository;
    private final TrashRepository trashRepository;

    @Override
    @Transactional
    public SimpleResponse createPresentation(Long lessonId, PresentationRequest presentationRequest) {
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new NotFoundException("Урок с id: " + lessonId + " не существует!"));
        for (Presentation presentation : lesson.getPresentations()) {
            if (presentation.getTitle().equals(presentationRequest.getTitle())) {
                throw new AlreadyExistsException("Презентация с названием " + presentation.getTitle() + " уже существует!");

            }
        }
        List<Presentation> notNullTrashPresentation = presentationRepository.findNotNullTrashPresentations();
        for (Presentation presentation : notNullTrashPresentation) {
            if (presentationRequest.getTitle().equals(presentation.getTitle())) {
                throw new AlreadyExistsException("Презентация с названием " + presentationRequest.getTitle() + " уже есть в корзине!");
            }
        }
        Presentation presentation = new Presentation();
        presentation.setTitle(presentationRequest.getTitle());
        presentation.setDescription(presentationRequest.getDescription());
        presentation.setFile(presentationRequest.getFile());

        lesson.getPresentations().add(presentation);
        presentationRepository.save(presentation);
        lessonRepository.save(lesson);
        return SimpleResponse.builder()
                .httpStatus(HttpStatus.OK)
                .message("Успешно загружено!")
                .build();
    }

    @Override
    @Transactional
    public SimpleResponse editPresentation(Long presentationId,
                                           EditPresentationRequest presentationRequest) {
        Presentation presentation = presentationRepository.findById(presentationId)
                .orElseThrow(() -> new NotFoundException("Презентация с id:  " + presentationId + " не существует!"));
        Lesson lesson = lessonRepository.findLessonByPresentationId(presentation.getId());
        for (Presentation presentation1 : lesson.getPresentations()) {
            if (presentation.getTitle().equals(presentationRequest.getTitle())) {
                throw new AlreadyExistsException("Презентация с названием " + presentation1.getTitle() + " уже существует!");
            }
        }
        List<Presentation> notNullTrashPresentation = presentationRepository.findNotNullTrashPresentations();
        for (Presentation presentation1 : notNullTrashPresentation) {
            if (presentationRequest.getTitle().equals(presentation1.getTitle())) {
                throw new AlreadyExistsException("Презентация с названием " + presentationRequest.getTitle() + " уже есть в корзине!");
            }
        }
            presentation.setDescription(presentationRequest.getDescription());
            presentation.setFile(presentationRequest.getFile());
        presentationRepository.save(presentation);
        return SimpleResponse.builder()
                .httpStatus(HttpStatus.OK)
                .message("Презентация успешно обновлена!")
                .build();
    }

    @Override
    public PresentationResponse findById(Long presentationId) {
        Presentation presentation = presentationRepository.findById(presentationId).orElseThrow(() -> new NotFoundException("Презентация с id: " + presentationId + " не найден!"));
        return PresentationResponse.builder()
                .id(presentation.getId())
                .title(presentation.getTitle())
                .description(presentation.getDescription())
                .file(presentation.getFile())
                .build();
    }

    @Override
    @Transactional
    public SimpleResponse deletePresentationById(Long presentationId) {
        Presentation presentation = presentationRepository.findById(presentationId).orElseThrow(() -> new NotFoundException("Презентация с id: " + presentationId + "не найден!"));
        presentationRepository.deletePresentation(presentationId);
        Trash trash = new Trash();
        trash.setName(presentation.getTitle());
        trash.setDateOfDelete(ZonedDateTime.now());
        trash.setType(Type.PRESENTATION);
        trash.setPresentation(presentation);
        presentation.setTrash(trash);
        trashRepository.save(trash);
        presentationRepository.save(presentation);
        return SimpleResponse.builder()
                .httpStatus(HttpStatus.OK)
                .message("Презентация успешно удален!")
                .build();
    }

    @Override
    public List<PresentationResponse> findAllPresentationByLessonId(Long lessonId) {
        return  presentationRepository.findAllPresentationsByLesson(lessonId);
    }
}
