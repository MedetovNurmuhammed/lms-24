package lms.service.impl;

import jakarta.transaction.Transactional;
import lms.dto.response.SimpleResponse;
import lms.entities.Option;
import lms.entities.Question;
import lms.entities.ResultTest;
import lms.exceptions.NotFoundException;
import lms.repository.OptionRepository;
import lms.repository.QuestionRepository;
import lms.service.QuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class QuestionServiceImpl implements QuestionService {
    private final QuestionRepository questionRepository;
    private final OptionRepository optionRepository;

    @Override
    @Transactional
    public SimpleResponse delete(Long questionId) {
            Question question = questionRepository.findById(questionId).
                    orElseThrow(() -> new NotFoundException("Вопрос не найден!!!"));

            // Удаление связанных записей из таблицы result_tests_options
        for (Option option : question.getOptions()) {
            optionRepository.deleteOptionById(option.getId());
        }
            // Удаление вариантов ответа
            optionRepository.deleteAll(question.getOptions());

            // Удаление связи с тестом и самого вопроса
            question.setTest(null);
            questionRepository.delete(question);

            return SimpleResponse.builder()
                    .httpStatus(HttpStatus.OK)
                    .message("Вопрос успешно удален!!")
                    .build();
        }
}
