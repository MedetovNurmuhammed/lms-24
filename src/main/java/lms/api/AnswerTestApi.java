package lms.api;

import io.swagger.v3.oas.annotations.Operation;
import lms.dto.request.AnswerTestRequest;
import lms.dto.response.AllQuestionResponse;
import lms.dto.response.ResultTestResponse;
import lms.service.ResultTestService;
import lms.service.QuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/answerTest")
@RequiredArgsConstructor
public class AnswerTestApi {
    private final ResultTestService resultTestService;
    private final QuestionService questionService;
    @Secured("STUDENT")
    @GetMapping("/findAllQuestionByTestId/{testId}")
    @Operation(description = "Возвращает все вопросы теста")
    public AllQuestionResponse findAllQuestion(@PathVariable Long testId) {
        return questionService.findAllQuestions(testId);
    }
    @Secured("STUDENT")
    @PostMapping("/answerTest/{testId}")
    @Operation(description = "ответы на вопросы и ответ")
    public ResultTestResponse resultTest(@PathVariable Long testId, @RequestBody AnswerTestRequest answerRequest) {
        return resultTestService.result(testId, answerRequest);
    }
    @Secured("STUDENT")
    @GetMapping("/findResultTestOfCurrentStudent")
    @Operation(description = "сохраненные результаты теста")
    public ResultTestResponse findResultOfCurrentStudent() {
        return resultTestService.findResultOfCurrentStudent();
    }
}
