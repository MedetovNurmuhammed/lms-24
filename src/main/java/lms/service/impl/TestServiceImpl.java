package lms.service.impl;

import jakarta.persistence.Table;
import jakarta.transaction.Transactional;
import lms.dto.request.OptionRequest;
import lms.dto.request.QuestionRequest;
import lms.dto.request.TestRequest;
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

@Service
@RequiredArgsConstructor
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
            question.setTitle(questionRequest.titlebek());
            question.setQuestionType(questionRequest.questionType());
            question.setPoint(questionRequest.pointbek());
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
    public SimpleResponse update(Long testId, TestRequest testRequest) {
        Test test = testRepository.findById(testId).
                orElseThrow(() -> new NotFoundException("Not found test "));
        test.setTitle(testRequest.title());
        test.setIsActive(false);

        return null;
    }
}
