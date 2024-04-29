package lms.service.impl;

import jakarta.transaction.Transactional;
import lms.dto.response.SimpleResponse;
import lms.entities.Link;
import lms.entities.Trash;
import lms.enums.Type;
import lms.exceptions.NotFoundException;
import lms.repository.LinkRepository;
import lms.repository.TrashRepository;
import lms.service.LinkService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class LinkServiceImpl implements LinkService {
    private final LinkRepository linkRepository;
    private final TrashRepository trashRepository;

    @Override
    @Transactional
    public SimpleResponse delete(Long linkId) {
        Link link = linkRepository.findById(linkId).
                orElseThrow(() -> new NotFoundException("Cсылка не найдена!"));
        Trash trash = new Trash();
        trash.setName(link.getTitle());
        trash.setType(Type.LINK);
        trash.setDateOfDelete(ZonedDateTime.now());
        trash.setLink(link);
        link.setTrash(trash);
        trashRepository.save(trash);
        return SimpleResponse
                .builder()
                .httpStatus(HttpStatus.OK)
                .message("Успешно добавлено в корзину")
                .build();
    }
}
