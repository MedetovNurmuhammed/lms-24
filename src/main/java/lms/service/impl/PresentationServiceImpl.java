package lms.service.impl;

import jakarta.transaction.TransactionScoped;
import jakarta.transaction.Transactional;
import lms.dto.response.SimpleResponse;
import lms.entities.Presentation;
import lms.entities.Trash;
import lms.enums.Type;
import lms.exceptions.NotFoundException;
import lms.repository.PresentationRepository;
import lms.repository.TrashRepository;
import lms.service.PresentationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class PresentationServiceImpl implements PresentationService {
    private final PresentationRepository presentationRepository;
    private final TrashRepository trashRepository;

    @Override
    @Transactional
    public SimpleResponse delete(Long presentationId) {
        Presentation presentation = presentationRepository.findById(presentationId).
                orElseThrow(() -> new NotFoundException("презентация не найдена!"));
        Trash trash = new Trash();
        trash.setName(presentation.getTitle());
        trash.setType(Type.PRESENTATION);
        trash.setDateOfDelete(ZonedDateTime.now());
        trash.setPresentation(presentation);
        presentation.setTrash(trash);
        trashRepository.save(trash);
        return SimpleResponse.builder()
                .httpStatus(HttpStatus.OK)
                .message("Успешно добавлено в корзину!")
                .build();
    }
}
