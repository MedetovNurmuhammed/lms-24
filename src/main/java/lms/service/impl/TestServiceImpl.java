package lms.service.impl;

import jakarta.transaction.Transactional;
import lms.dto.response.SimpleResponse;
import lms.entities.Test;
import lms.entities.Trash;
import lms.enums.Type;
import lms.exceptions.NotFoundException;
import lms.repository.TestRepository;
import lms.repository.TrashRepository;
import lms.service.TestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class TestServiceImpl implements TestService {
    private final TestRepository testRepository;
    private final TrashRepository trashRepository;

    @Override
    @Transactional
    public SimpleResponse delete(Long testId) {
        Test test = testRepository.findById(testId).
                orElseThrow(() -> new NotFoundException("Тест не найден!"));
        Trash trash = new Trash();
        trash.setName(test.getTitle());
        trash.setType(Type.TEST);
        trash.setDateOfDelete(ZonedDateTime.now());
        trash.setTest(test);
        test.setTrash(trash);
        trashRepository.save(trash);
        return SimpleResponse.builder()
                .httpStatus(HttpStatus.OK)
                .message("Успешно добавлено в корзину")
                .build();
    }
}
