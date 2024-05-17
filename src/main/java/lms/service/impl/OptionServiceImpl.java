package lms.service.impl;

import jakarta.transaction.Transactional;
import lms.dto.response.SimpleResponse;
import lms.entities.Option;
import lms.exceptions.NotFoundException;
import lms.repository.OptionRepository;
import lms.service.OptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OptionServiceImpl implements OptionService {
    private final OptionRepository optionRepository;

    @Override
    @Transactional
    public SimpleResponse deleteOption(Long optionId) {
        Option option = optionRepository.findById(optionId).
                orElseThrow(() -> new NotFoundException("Вариант-ответ  не найден!  "));
        option.setQuestion(null);
        optionRepository.deleteOptionById(optionId);
        optionRepository.delete(option);
        return SimpleResponse.builder()
                .httpStatus(HttpStatus.OK)
                .message("Успешно удалено!")
                .build();
    }
}
