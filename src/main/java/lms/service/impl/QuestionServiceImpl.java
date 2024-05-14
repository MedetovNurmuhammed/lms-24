package lms.service.impl;

import lms.dto.response.AllQuestionResponse;
import lms.dto.response.OptionResponse;
import lms.dto.response.QuestionResponse;
import lms.entities.Option;
import lms.entities.Question;
import lms.repository.QuestionRepository;
import lms.service.QuestionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Validated
@Slf4j
public class QuestionServiceImpl implements QuestionService {
    private final QuestionRepository questionRepository;

    @Override
    public AllQuestionResponse findAllQuestions(Long testId) {
        List<Question> questions = questionRepository.findAllByTestId(testId);
        List<QuestionResponse> questionResponses = new ArrayList<>();
        List<OptionResponse> optionResponses = new ArrayList<>();
        for (Question question : questions) {
            for (Option option : question.getOptions()) {
                optionResponses.add(new OptionResponse(option.getId(), option.getOption()));
            }
            questionResponses.add(new QuestionResponse(question.getId(), question.getTitle(), optionResponses));
        }
        return AllQuestionResponse.builder()
                .questionResponses(questionResponses)
                .build();
    }
}
