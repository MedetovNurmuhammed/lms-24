package lms.api;

import io.swagger.v3.oas.annotations.Operation;
import lms.dto.response.QuestionResponse;
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
    public QuestionResponse findAllQuestion(@PathVariable Long testId) {
        return questionService.findAllQuestions(testId);
    }
}
