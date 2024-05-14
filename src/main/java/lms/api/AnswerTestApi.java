package lms.api;

import io.swagger.v3.oas.annotations.Operation;
import lms.dto.request.AnswerTestRequest;
import lms.dto.response.ResultTestResponse;
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
    @PostMapping("/answerTest/{testId}")
    @Operation(summary = "ответы на вопросы и ответ")
    public ResultTestResponse resultTest(@PathVariable Long testId, @RequestBody AnswerTestRequest answerRequest) {
        return resultTestService.result(testId, answerRequest);
    }
    @Secured("STUDENT")
    @GetMapping("/findResultTestOfCurrentStudent")
    @Operation(summary = "сохраненные результаты теста")
    public ResultTestResponse findResultOfCurrentStudent() {
        return resultTestService.findResultOfCurrentStudent();
    }

    @Secured("INSTRUCTOR")
    @GetMapping("/findResultTestBuId/{resultTestId}")
    @Operation(summary = "возвращает результат теста.(Авторизация: инструктор)")
    public ResultTestResponse findResultTestById(@PathVariable Long resultTestId) {
        return resultTestService.findResultTestById(resultTestId);
    }

}
