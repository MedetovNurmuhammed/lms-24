package lms.service.impl;


import jakarta.transaction.Transactional;
import lms.dto.request.AnswerTestRequest;
import lms.dto.response.AnswerOptionResponse;
import lms.dto.response.AnswerQuestionResponse;
import lms.dto.response.ResultTestResponse;
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
    public ResultTestResponse result(Long testId, AnswerTestRequest answerRequest) {

        Test test = testRepository.findById(testId).orElseThrow(() -> new NotFoundException("тест с  id " + testId + " не найден"));
        double totalPoint = 0;
        ResultTest resultTest = new ResultTest();
        String emailCurrentUser = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(emailCurrentUser).orElseThrow(() -> new NotFoundException("студент с  email " + emailCurrentUser + " не найден"));
        Student studentByUserId = studentRepository.findStudentByUserId(user.getId()).orElseThrow(() -> new NotFoundException("студент с  id " + user.getId() + " не найден"));
        resultTest.setStudent(studentByUserId);
        resultTest.setTest(test);
        test.getResultTests().add(resultTest);
        studentByUserId.getResultTests().add(resultTest);
        ResultTestResponse resultTestResponse = new ResultTestResponse();

        for (Question question : test.getQuestions()) {
            AnswerQuestionResponse answerQuestionResponse = new AnswerQuestionResponse();
            answerQuestionResponse.setQuestionId(question.getId());
            answerQuestionResponse.setQuestionTitle(question.getTitle());
            answerQuestionResponse.setQuestionType(question.getQuestionType());
            answerQuestionResponse.setPoint(question.getPoint());
            for (Option option : question.getOptions()) {
                AnswerOptionResponse answerOptionResponse = new AnswerOptionResponse();
                answerOptionResponse.setOptionId(option.getId());
                answerOptionResponse.setTrue(option.getIsTrue());
                for (Long optionId : answerRequest.optionId()) {
                    optionRepository.findById(optionId).orElseThrow(() -> new NotFoundException("ответ с id: " + optionId + " не найден"));
                    if (optionId.equals(option.getId())) {
                        resultTest.getOptions().add(option);
                        if (option.getIsTrue()) {
                            answerOptionResponse.setYourChoice(true);
                        } else {
                            answerOptionResponse.setYourChoice(false);
                        }
                        answerQuestionResponse.setPoint(option.getOptionPoint());
                        totalPoint += option.getOptionPoint();
                    }
                }
                answerOptionResponse.setOption(option.getOption());
                answerQuestionResponse.getAnswerOptionResponses().add(answerOptionResponse);
            }
            resultTestRepository.save(resultTest);
            resultTestResponse.setTestId(testId);
            resultTestResponse.setTestTitle(test.getTitle());
            resultTestResponse.setTotalPoint(totalPoint);

            resultTestResponse.getAnswerQuestionResponses().add(answerQuestionResponse);
        }
        return resultTestResponse;
    }

    @Override
    public ResultTestResponse findResultOfCurrentStudent(Long testId) {
        String emailCurrentUser = SecurityContextHolder.getContext().getAuthentication().getName();
        double totalPoint = 0;
        Test test = testRepository.findById(testId).orElseThrow(() -> new NotFoundException("тест с айди: " + testId + " не найден"));
        User user = userRepository.findByEmail(emailCurrentUser).orElseThrow(() -> new NotFoundException("студент с  email " + emailCurrentUser + " не найден"));
        Student studentByUserId = studentRepository.findStudentByUserId(user.getId()).orElseThrow(() -> new NotFoundException(""));
        ResultTest resultTest = resultTestRepository.findByStudentId(studentByUserId.getId());
        ResultTestResponse resultTestResponse = new ResultTestResponse();
        for (Question question : test.getQuestions()) {
            AnswerQuestionResponse answerQuestionResponse = new AnswerQuestionResponse();
            answerQuestionResponse.setQuestionId(question.getId());
            answerQuestionResponse.setQuestionTitle(question.getTitle());
            answerQuestionResponse.setQuestionType(question.getQuestionType());
            answerQuestionResponse.setPoint(question.getPoint());
            for (Option option : question.getOptions()) {
                AnswerOptionResponse answerOptionResponse = new AnswerOptionResponse();
                answerOptionResponse.setOptionId(option.getId());
                answerOptionResponse.setTrue(option.getIsTrue());
                for (Option optionId : resultTest.getOptions()) {
                    answerQuestionResponse.setPoint(option.getOptionPoint());
                    totalPoint += optionId.getOptionPoint();
                }
                answerOptionResponse.setOption(option.getOption());
                answerQuestionResponse.getAnswerOptionResponses().add(answerOptionResponse);
            }
            resultTestResponse.setTestId(test.getId());
            resultTestResponse.setTestTitle(test.getTitle());
            resultTestResponse.setTotalPoint(totalPoint);
            resultTestResponse.getAnswerQuestionResponses().add(answerQuestionResponse);
        }
        return resultTestResponse;
    }

    @Override
    public ResultTestResponse findResultTestById(Long resultTestId) {
        double totalPoint = 0;
        ResultTest resultTest = resultTestRepository.findById(resultTestId).orElseThrow(() -> new NotFoundException("результат с " + resultTestId + " айди не найден"));
        ResultTestResponse resultTestResponse = new ResultTestResponse();
        for (Question question : resultTest.getTest().getQuestions()) {
            AnswerQuestionResponse answerQuestionResponse = new AnswerQuestionResponse();
            answerQuestionResponse.setQuestionId(question.getId());
            answerQuestionResponse.setQuestionTitle(question.getTitle());
            answerQuestionResponse.setQuestionType(question.getQuestionType());
            answerQuestionResponse.setPoint(question.getPoint());
            for (Option option : question.getOptions()) {
                AnswerOptionResponse answerOptionResponse = new AnswerOptionResponse();
                answerOptionResponse.setOptionId(option.getId());
                answerOptionResponse.setTrue(option.getIsTrue());
                for (Option optionId : resultTest.getOptions()) {
                    answerQuestionResponse.setPoint(option.getOptionPoint());
                    totalPoint += optionId.getOptionPoint();
                }
                answerOptionResponse.setOption(option.getOption());
                answerQuestionResponse.getAnswerOptionResponses().add(answerOptionResponse);
            }
            resultTestResponse.setTestId(resultTest.getTest().getId());
            resultTestResponse.setTestTitle(resultTest.getTest().getTitle());
            resultTestResponse.setTotalPoint(totalPoint);
            resultTestResponse.getAnswerQuestionResponses().add(answerQuestionResponse);
        }
        return resultTestResponse;
    }
}




