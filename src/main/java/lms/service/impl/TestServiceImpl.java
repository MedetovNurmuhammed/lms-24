package lms.service.impl;

import jakarta.transaction.Transactional;
import lms.dto.request.*;
import lms.dto.response.SimpleResponse;
import lms.entities.Lesson;
import lms.entities.Option;
import lms.entities.Question;
import lms.entities.Test;
import lms.exceptions.NotFoundException;
import lms.repository.LessonRepository;
import lms.repository.OptionRepository;
import lms.repository.QuestionRepository;
import lms.repository.TestRepository;
import lms.service.TestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional
public class TestServiceImpl implements TestService {
    private final TestRepository testRepository;
    private final LessonRepository lessonRepository;
    private final QuestionRepository questionRepository;
    private final OptionRepository optionRepository;

    @Override
    @Transactional
    public SimpleResponse saveTest(Long lessonId, TestRequest testRequest) {
        Lesson lesson = lessonRepository.findById(lessonId).
                orElseThrow(() -> new NotFoundException(" не найден!"));
        Test test = new Test();
        test.setTitle(testRequest.title());
        test.setIsActive(false);
        test.setCreationDate(LocalDate.now());
        test.setHour(testRequest.hour());
        test.setMinute(testRequest.minute());
        test.setLesson(lesson);
        lesson.setTest(test);
        testRepository.save(test);
        for (QuestionRequest questionRequest : testRequest.questionRequests()) {
            Question question = new Question();
            question.setTitle(questionRequest.title());
            question.setQuestionType(questionRequest.questionType());
            question.setPoint(questionRequest.point());
            question.setTest(test);
            test.getQuestions().add(question);
            questionRepository.save(question);
            for (OptionRequest optionRequest : questionRequest.optionRequests()) {
                Option option = new Option();
                option.setOption(optionRequest.option());
                option.setIsTrue(optionRequest.isTrue());
                option.setQuestion(question);
                question.getOptions().add(option);
                optionRepository.save(option);
            }
        }
        return SimpleResponse.builder()
                .httpStatus(HttpStatus.OK)
                .message("Тест успешно создан!!!")
                .build();
    }


    @Override
    @Transactional
    public SimpleResponse update(Long testId, UpdateTestRequest testRequest) {

        Test test = testRepository.findById(testId)
                .orElseThrow(() -> new NotFoundException("Test not found"));
        test.setTitle(testRequest.title());
        test.setIsActive(false);
        test.setHour(testRequest.hour());
        test.setMinute(testRequest.minute());

        for (UpdateQuestionRequest questionRequest : testRequest.updateQuestionRequests()) {

            Question question = questionRequest.questionId() != null ?
                    questionRepository.findById(questionRequest.questionId())
                            .orElseThrow(() -> new NotFoundException("Question not found")) :
                    new Question();

            question.setTitle(questionRequest.title());
            question.setQuestionType(questionRequest.questionType());
            question.setPoint(questionRequest.point());
            question.setTest(test);

            for (UpdateOptionRequest optionRequest : questionRequest.updateOptionRequest()) {
                Option option = optionRequest.optionId() != null ?
                        optionRepository.findById(optionRequest.optionId())
                                .orElseThrow(() -> new NotFoundException("Option not found")) :
                        new Option();

                option.setOption(optionRequest.option());
                option.setIsTrue(optionRequest.isTrue());
                option.setQuestion(question);
                optionRepository.save(option);
            }
            questionRepository.save(question);
        }
        testRepository.save(test);


        return SimpleResponse.builder()
                .httpStatus(HttpStatus.OK)
                .message("Test successfully updated")
                .build();
    }


    @Transactional
    public void updateOption(Long questionId, List<Long> optionId, UpdateOptionRequest updateOptionRequest) {
        Question question = questionRepository.findById(questionId).
                orElseThrow(() -> new NotFoundException("Не найден!"));
        for (Option option : question.getOptions()) {
            for (Long ids : optionId) {
                if (Objects.equals(option.getId(), ids)) {
                    option.setOption(updateOptionRequest.option());
                    option.setIsTrue(updateOptionRequest.isTrue());
//                    option.setQuestion(question);
//                    question.getOptions().add();
                }
            }
        }
        SimpleResponse.builder()
                .httpStatus(HttpStatus.OK)
                .message("Обновлен!")
                .build();
    }

    @Override
    @Transactional
    public SimpleResponse enableToStart(Long testId) {
        Test test = testRepository.findById(testId).
                orElseThrow(() -> new NotFoundException("Не найден!!!"));
        test.setIsActive(true);
        return SimpleResponse.builder()
                .httpStatus(HttpStatus.OK)
                .message("Готов к тесту")
                .build();
    }
}
