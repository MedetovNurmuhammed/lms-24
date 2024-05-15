package lms.service.impl;


import jakarta.transaction.Transactional;
import lms.dto.request.AnswerTestRequest;
import lms.dto.response.AnswerOptionResponse;
import lms.dto.response.AnswerQuestionResponse;
import lms.dto.response.ResultTestResponse;
import lms.dto.response.SimpleResponse;
import lms.entities.Test;
import lms.entities.ResultTest;
import lms.entities.User;
import lms.entities.Student;
import lms.entities.Question;
import lms.entities.Option;
import lms.exceptions.NotFoundException;
import lms.repository.ResultTestRepository;
import lms.repository.UserRepository;
import lms.repository.TestRepository;
import lms.repository.StudentRepository;
import lms.repository.OptionRepository;
import lms.service.ResultTestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Service
@RequiredArgsConstructor
@Validated
@Slf4j
public class ResultTestServiceImpl implements ResultTestService {
    private final ResultTestRepository resultTestRepository;
    private final TestRepository testRepository;
    private final UserRepository userRepository;
    private final StudentRepository studentRepository;
    private final OptionRepository optionRepository;

    @Override
    @Transactional
    public SimpleResponse saveResult(Long testId, AnswerTestRequest answerRequest) {
        Test test = testRepository.findById(testId)
                .orElseThrow(() -> new NotFoundException("тест с id " + testId + " не найден"));

        Student student = getCurrentStudent();
        ResultTest resultTest = new ResultTest();
        resultTest.setStudent(student);
        resultTest.setTest(test);
        test.getResultTests().add(resultTest);
        student.getResultTests().add(resultTest);
        for (Long optionId : answerRequest.optionId()) {
            Option option = optionRepository.findById(optionId)
                    .orElseThrow(() -> new NotFoundException("ответ с id: " + optionId + " не найден"));
            resultTest.getOptions().add(option);
        }
        resultTestRepository.save(resultTest);
        return SimpleResponse.builder()
                .httpStatus(HttpStatus.OK)
                .message("вы успешно сдали тест")
                .build();
    }
    @Override
    public ResultTestResponse findResultOfCurrentStudent(Long testId) {
        Test test = testRepository.findById(testId).orElseThrow(() -> new NotFoundException("тест с айди: " + testId + " не найден"));
        Student student = getCurrentStudent();
        ResultTest resultTest = resultTestRepository.findByStudentId(student.getId(), test.getId());
        return getResultTestResponse(resultTest);
    }
    @Override
    public ResultTestResponse findResultTestById(Long resultTestId) {
        ResultTest resultTest = resultTestRepository.findById(resultTestId).orElseThrow(() -> new NotFoundException("результат с " + resultTestId + "не найден"));

        return getResultTestResponse(resultTest);
    }
    private ResultTestResponse getResultTestResponse(ResultTest resultTest) {
        Test test = resultTest.getTest();
        ResultTestResponse resultTestResponse = new ResultTestResponse();
        double totalPoint = 0;
        for (Question question : test.getQuestions()) {
            AnswerQuestionResponse answerQuestionResponse = new AnswerQuestionResponse();
            answerQuestionResponse.setQuestionId(question.getId());
            answerQuestionResponse.setQuestionTitle(question.getTitle());
            answerQuestionResponse.setQuestionType(question.getQuestionType());
            answerQuestionResponse.setPoint(question.getPoint());
            int trueOptionSize = optionRepository.getTrueOptionSize(question.getId());
            int userChoiceSize = 0;
            double userPoint = 0;

            for (Option option : question.getOptions()) {
                AnswerOptionResponse answerOptionResponse = new AnswerOptionResponse();
                answerOptionResponse.setOptionId(option.getId());
                answerOptionResponse.setTrue(option.getIsTrue());
                if (resultTest.getOptions().contains(option)) {
                    if (option.getIsTrue()) {
                        answerOptionResponse.setYourChoice(true);
                        userChoiceSize++;
                    } else {
                        answerOptionResponse.setYourChoice(false);
                    }
                }
                answerOptionResponse.setOption(option.getOption());
                answerQuestionResponse.getAnswerOptionResponses().add(answerOptionResponse);
            }

            if (trueOptionSize == userChoiceSize) {
                userPoint = question.getPoint();
            } else {
                double optionPoint = question.getPoint() / trueOptionSize;
                userPoint = optionPoint * userChoiceSize;
            }

            answerQuestionResponse.setPoint(userPoint);
            resultTestResponse.getAnswerQuestionResponses().add(answerQuestionResponse);
            totalPoint += userPoint;
        }

        resultTestResponse.setTestId(test.getId());
        resultTestResponse.setTestTitle(test.getTitle());
        resultTestResponse.setTotalPoint(totalPoint);

        return resultTestResponse;
    }

    private Student getCurrentStudent() {
        String emailCurrentUser = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(emailCurrentUser).orElseThrow(() ->
                new NotFoundException("студент с  email " + emailCurrentUser + " не найден"));
        return studentRepository.findStudentByUserId(user.getId()).orElseThrow(() ->
                new NotFoundException("студент с  id " + user.getId() + " не найден"));

    }
}




