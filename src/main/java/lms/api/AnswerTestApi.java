package lms.api;

import io.swagger.v3.oas.annotations.Operation;
import lms.dto.request.AnswerTestRequest;
import lms.dto.response.ResultTestResponse;
import lms.dto.response.SimpleResponse;
import lms.service.ResultTestService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.GetMapping;


@RestController
@RequestMapping("/api/answerTest")
@RequiredArgsConstructor
public class AnswerTestApi {
    private final ResultTestService resultTestService;

    @Secured("STUDENT")
    @PostMapping("/pastTest/{testId}")
    @Operation(summary = "ответы на вопросы и ответ.(Авторизация: студент)")
    public SimpleResponse resultTest(@PathVariable Long testId, @RequestBody AnswerTestRequest answerRequest) {
        return resultTestService.saveResult(testId, answerRequest);
    }
    @Secured("STUDENT")
    @GetMapping("/myResultTest/{testId}")
    @Operation(summary = "сохраненные результаты теста.(Авторизация: студент)")
    public ResultTestResponse findResultOfCurrentStudent(@PathVariable Long testId) {
        return resultTestService.findResultOfCurrentStudent(testId);
    }

    @Secured("INSTRUCTOR")
    @GetMapping("/resultTestOfStudent/{resultTestId}")
    @Operation(summary = "возвращает результат теста.(Авторизация: инструктор)")
    public ResultTestResponse findResultTestById(@PathVariable Long resultTestId) {
        return resultTestService.findResultTestById(resultTestId);
    }

}
