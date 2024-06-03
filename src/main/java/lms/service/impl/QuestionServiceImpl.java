package lms.service.impl;

import jakarta.transaction.Transactional;
import lms.dto.response.SimpleResponse;
import lms.entities.Option;
import lms.entities.Question;
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
        Question question = questionRepository.findQuestionById(questionId).
                orElseThrow(() -> new NotFoundException("Вопрос не найден!!!"));

        for (Option option : question.getOptions()) {
            optionRepository.deleteOptionById(option.getId());
        }
        optionRepository.deleteAll(question.getOptions());
        question.setTest(null);
        questionRepository.delete(question);

        return SimpleResponse.builder()
                .httpStatus(HttpStatus.OK)
                .message("Вопрос успешно удален!!")
                .build();
    }
}
