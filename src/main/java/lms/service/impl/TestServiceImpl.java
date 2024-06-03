package lms.service.impl;

import jakarta.transaction.Transactional;
import lms.dto.request.*;
import lms.dto.response.*;
import lms.entities.*;
import lms.enums.Type;
import lms.exceptions.NotFoundException;
import lms.repository.*;
import lms.repository.jdbcTemplateService.TestJDBCTemplate;
import lms.service.TestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class TestServiceImpl implements TestService {
    private final TestRepository testRepository;
    private final LessonRepository lessonRepository;
    private final QuestionRepository questionRepository;
    private final OptionRepository optionRepository;
    private final TrashRepository trashRepository;
    private final TestJDBCTemplate testJDBCTemplate;

    @Override
    @Transactional
    public SimpleResponse saveTest(Long lessonId, TestRequest testRequest) {
        Lesson lesson = lessonRepository.findLessonById(lessonId).
                orElseThrow(() -> new NotFoundException("Урок не найден!"));
        Test test = new Test();
        test.setTitle(testRequest.title());
        test.setIsActive(false);
        test.setCreationDate(LocalDate.now());
        test.setHour(testRequest.hour());
        test.setMinute(testRequest.minute());
        test.setLesson(lesson);
        lesson.getTests().add(test);
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
                .message("Тест успешно создан!")
                .build();
    }


    @Override
    @Transactional
    public SimpleResponse update(Long testId, UpdateTestRequest testRequest) {

        Test test = testRepository.findTestById(testId)
                .orElseThrow(() -> new NotFoundException("Тест не найден! "));
        test.setTitle(testRequest.title());
        test.setIsActive(false);
        test.setHour(testRequest.hour());
        test.setMinute(testRequest.minute());

        for (UpdateQuestionRequest questionRequest : testRequest.updateQuestionRequests()) {

            Question question = questionRequest.questionId() != null ?
                    questionRepository.findQuestionById(questionRequest.questionId())
                            .orElseThrow(() -> new NotFoundException("Вопрос не найден! ")) :
                    new Question();

            question.setTitle(questionRequest.title());
            question.setQuestionType(questionRequest.questionType());
            question.setPoint(questionRequest.point());
            question.setTest(test);

            for (UpdateOptionRequest optionRequest : questionRequest.updateOptionRequest()) {
                Option option = optionRequest.optionId() != null ?
                        optionRepository.findOptionById(optionRequest.optionId())
                                .orElseThrow(() -> new NotFoundException("Вариант-ответ не найден! ")) :
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
                .message("Тест успешно обновлен!")
                .build();
    }

    @Override
    @Transactional
    public SimpleResponse accessToTest(Long testId) {
        Test test = testRepository.findTestById(testId).
                orElseThrow(() -> new NotFoundException("Не найден!!!"));

        if (test.getIsActive().equals(false)) {
            test.setIsActive(true);
            return SimpleResponse.builder()
                    .httpStatus(HttpStatus.OK)
                    .message("Готов к тесту.")
                    .build();
        } else {
            test.setIsActive(false);
            return SimpleResponse.builder()
                    .httpStatus(HttpStatus.OK)
                    .message("Доступ к тесту запрешен.")
                    .build();
        }
    }

    @Override
    public SimpleResponse delete(Long testId) {
        Test test = testRepository.findTestById(testId).
                orElseThrow(() -> new NotFoundException("Тест не найден!!!"));
        Trash trash = new Trash();
        trash.setName(test.getTitle());
        trash.setType(Type.TEST);
        trash.setTest(test);
        trashRepository.save(trash);
        test.setTrash(trash);
        return SimpleResponse.builder()
                .message("Успешно добавлено в корзину!")
                .httpStatus(HttpStatus.OK)
                .build();
    }

    @Override
    public TestResponseWithStudents findById(Long testId) {
         testRepository.findTestById(testId).
                orElseThrow(() -> new NotFoundException("Тест не найден!!!"));
        List<StudentTestResponse> responses = testJDBCTemplate.allStudentsWithResultTest(testId);
        return TestResponseWithStudents.builder()
                .id(testId)
                .studentTestResponses(responses)
                .build();
    }

    public TestResponse findTestByIdForEdit(Long testId) {
        Test test = testRepository.findById(testId)
                .orElseThrow(() -> new NotFoundException("Тест не найден!!!"));
        List<Question> questions = test.getQuestions();
        List<QuestionResponse> questionResponses = questions.stream()
                .map(this::mapToQuestionResponse)
                .collect(Collectors.toList());

        return TestResponse.builder().testId(test.getId())
                .title(test.getTitle())
                .hour(test.getHour())
                .minute(test.getMinute())
                .questionResponseList(questionResponses)
                .build();
    }

    @Override
    public AllTestResponse findAll(Long lessonId) {
      lessonRepository.findLessonById(lessonId).
                orElseThrow(() -> new NotFoundException("Урок не найден!!!"));
        List<TestResponseForGetAll> testResponseForGetAll = testRepository.findAllTestsByLessonId(lessonId);
        return AllTestResponse.builder()
                .testResponseForGetAll(testResponseForGetAll)
                .build();
    }

    private QuestionResponse mapToQuestionResponse(Question question) {
        List<OptionResponse> optionResponses = question.getOptions().stream()
                .map(option -> new OptionResponse(option.getId(),option.getOption(), option.getIsTrue()))
                .collect(Collectors.toList());

        return new QuestionResponse(
                question.getId(),
                question.getTitle(),
                question.getPoint(),
                question.getQuestionType(),
                optionResponses
        );
    }
}
