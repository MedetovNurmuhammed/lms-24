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
import lms.exceptions.BadRequestException;
import lms.exceptions.NotFoundException;
import lms.repository.*;
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
        Lesson lesson = lessonRepository.findLessonById(lessonId)
                .orElseThrow(() -> new NotFoundException("Урок с id: " + lessonId + " не существует!"));
        if (lesson.getTrash() == null) {
            boolean exists = presentationRepository.existsTitle(lesson.getId(), presentationRequest.getTitle());
            if (exists) {
                throw new AlreadyExistsException("Презентация с названием " + presentationRequest.getTitle() + " уже существует!");
            }
            boolean notNullTrashPresentations = presentationRepository.existsNotNullTrashPresentation(lesson.getId(), presentationRequest.getTitle());
            if (notNullTrashPresentations)
                throw new AlreadyExistsException("Презентация с названием " + presentationRequest.getTitle() + " уже есть в корзине!");
            Presentation presentation = new Presentation();
            presentation.setTitle(presentationRequest.getTitle());
            presentation.setDescription(presentationRequest.getDescription());
            presentation.setFile(presentationRequest.getFile());
            presentation.setLesson(lesson);
            presentationRepository.save(presentation);
            lesson.getPresentations().add(presentation);
            lessonRepository.save(lesson);
            return SimpleResponse.builder()
                    .httpStatus(HttpStatus.OK)
                    .message("Успешно загружено!")
                    .build();
        } else throw new BadRequestException("Урок может быть в корзине!");
    }

    @Override
    @Transactional
    public SimpleResponse editPresentation(Long presentationId,
                                           EditPresentationRequest presentationRequest) {
        Presentation presentation = presentationRepository.findPresentationById(presentationId)
                .orElseThrow(() -> new NotFoundException("Презентация с id:  " + presentationId + " не существует!"));
        if (presentation.getTrash() == null) {
            Lesson lesson = lessonRepository.findLessonByPresentationId(presentation.getId());
            if (!presentation.getTitle().equals(presentationRequest.getTitle())) {
                boolean exists = presentationRepository.existsTitle(lesson.getId(), presentationRequest.getTitle());
                if (exists) {
                    throw new AlreadyExistsException("Презентация с названием " + presentationRequest.getTitle() + " уже существует!");
                }
            }
            boolean notNullTrashPresentations = presentationRepository.existsNotNullTrashPresentation(lesson.getId(), presentationRequest.getTitle());
            if (notNullTrashPresentations)
                throw new AlreadyExistsException("Презентация с названием " + presentationRequest.getTitle() + " уже есть в корзине!");
            presentation.setTitle(presentationRequest.getTitle());
            presentation.setDescription(presentationRequest.getDescription());
            presentation.setFile(presentationRequest.getFile());
            presentationRepository.save(presentation);
            return SimpleResponse.builder()
                    .httpStatus(HttpStatus.OK)
                    .message("Презентация успешно обновлена!")
                    .build();
        } else throw new BadRequestException("Презентация может быть в корзине!");
    }

    @Override
    public PresentationResponse findById(Long presentationId) {
        Presentation presentation = presentationRepository.findPresentationById(presentationId).orElseThrow(() -> new NotFoundException("Презентация с id: " + presentationId + " не найден!"));
        if (presentation.getTrash() == null) {
            return PresentationResponse.builder()
                    .id(presentation.getId())
                    .title(presentation.getTitle())
                    .description(presentation.getDescription())
                    .file(presentation.getFile())
                    .build();
        } else throw new BadRequestException("Презентация может быть в корзине!");
    }

    @Override
    @Transactional
    public SimpleResponse deletePresentationById(Long presentationId) {
        Presentation presentation = presentationRepository.findPresentationById(presentationId).orElseThrow(() -> new NotFoundException("Презентация с id: " + presentationId + "не найден!"));
        if (presentation.getTrash() == null) {
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
        }else throw new BadRequestException("Презентация может быть в корзине!");
    }

    @Override
    public List<PresentationResponse> findAllPresentationByLessonId(Long lessonId) {
        return presentationRepository.findAllPresentationsByLesson(lessonId);
    }
}
