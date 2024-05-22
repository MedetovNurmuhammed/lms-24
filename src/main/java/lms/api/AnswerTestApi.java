package lms.api;

import io.swagger.v3.oas.annotations.Operation;
import lms.dto.request.AnswerTestRequest;
import lms.dto.response.ResultTestResponse;
import lms.dto.response.SimpleResponse;
import lms.service.ResultTestService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/answerTest")
@RequiredArgsConstructor
@CrossOrigin(origins = "*",maxAge = 3600)
public class AnswerTestApi {
    private final ResultTestService resultTestService;

    @Secured("STUDENT")
    @PostMapping("/{testId}")
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
