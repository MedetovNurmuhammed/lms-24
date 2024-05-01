package lms.service.impl;

import jakarta.transaction.Transactional;
import lms.aws.service.StorageService;
import lms.dto.request.EditPresentationRequest;
import lms.dto.request.PresentationRequest;
import lms.dto.response.PresentationResponse;
import lms.dto.response.SimpleResponse;
import lms.entities.Lesson;
import lms.entities.Presentation;
import lms.exceptions.NotFoundException;
import lms.repository.LessonRepository;
import lms.repository.PresentationRepository;
import lms.service.PresentationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Validated
@Slf4j
public class PresentationServiceImpl implements PresentationService {
    private final LessonRepository lessonRepository;
    private final StorageService storageService;
    private final PresentationRepository presentationRepository;

    @Override
    @Transactional
    public SimpleResponse createPresentation(Long lessonId, PresentationRequest presentationRequest, MultipartFile file) {
        Lesson lesson = lessonRepository.findById(lessonId).orElseThrow(() -> new NotFoundException("Урок с id: " + lessonId + "не существует!"));
        if (!file.getContentType().equals("application/pdf")) {
            throw new IllegalArgumentException("Файл должен быть формата PDF");
        }
        Presentation presentation = new Presentation();
        presentation.setTitle(presentationRequest.getTitle());
        presentation.setDescription(presentationRequest.getDescription());
        String url = storageService.uploadFile(file);
        presentation.setFile(url);
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
    public SimpleResponse editPresentation(Long presentationId, EditPresentationRequest presentationRequest, MultipartFile file) {
        Presentation presentation = presentationRepository.findById(presentationId)
                .orElseThrow(() -> new NotFoundException("Презентация с id: " + presentationId + " не существует!"));

        if (presentationRequest.getTitle() != null) {
            presentation.setTitle(presentationRequest.getTitle());
        }
        if (presentationRequest.getDescription() != null) {
            presentation.setDescription(presentationRequest.getDescription());
        }


        if (!file.isEmpty()) {
            storageService.deleteFile(presentation.getFile());
        }
        if (!file.getContentType().equals("application/pdf")) {
            throw new IllegalArgumentException("Файл должен быть формата PDF");
        }
        String url = storageService.uploadFile(file);
        presentation.setFile(url);
        presentationRepository.save(presentation);

        return SimpleResponse.builder()
                .httpStatus(HttpStatus.OK)
                .message("Презентация успешно обновлена!")
                .build();
    }

    @Override
    public PresentationResponse findById(Long presentationId) {
Presentation presentation = presentationRepository.findById(presentationId).orElseThrow(()->new NotFoundException("Презентация с id: "+presentationId+" не найден!"));
        return PresentationResponse.builder()
                .id(presentation.getId())
                .title(presentation.getTitle())
                .description(presentation.getDescription())
                .file(presentation.getFile())
                .build();
    }

    @Override
    public SimpleResponse deletePresentationById(Long presentationId) {
        Presentation presentation = presentationRepository.findById(presentationId).orElseThrow(()->new NotFoundException("Презентация с id: "+presentationId+ "не найден!"));
        presentationRepository.deleteById(presentationId);
        storageService.deleteFile(presentation.getFile());
        return SimpleResponse.builder()
                .httpStatus(HttpStatus.OK)
                .message("Презентация успешно удален!")
                .build();
    }
}
