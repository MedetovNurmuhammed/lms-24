package lms.api;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lms.dto.request.QuestionRequest;
import lms.dto.request.TestRequest;
import lms.dto.response.SimpleResponse;
import lms.entities.Question;
import lms.service.TestService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/test")
@RequiredArgsConstructor
public class TestApi {
    private final TestService testService;

    @Secured("INSTRUCTOR")
    @Operation(summary = "Сохранить теста",
            description = "Метод для сохранение теста " +
                    " Авторизация: администратор!")
    @PostMapping("/save/{lessonId}")
    public SimpleResponse createTest (@PathVariable Long lessonId,
                                      @RequestBody @Valid TestRequest testRequest){
    return testService.saveTest(lessonId,testRequest);
    }

    @Secured("INSTRUCTOR")
    @Operation(summary = "Обновить теста",
            description = "Метод для обновление теста " +
                    " Авторизация: администратор!")
    @PutMapping("/save/{testId}")
    public SimpleResponse update (@PathVariable Long testId,
                                  @RequestBody TestRequest testRequest){
        return testService.update(testId,testRequest);
    }
}
