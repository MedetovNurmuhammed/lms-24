package lms.api;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lms.dto.request.QuestionRequest;
import lms.dto.request.TestRequest;
import lms.dto.request.UpdateTestRequest;
import lms.dto.response.SimpleResponse;
import lms.dto.response.TestResponse;
import lms.entities.Question;
import lms.service.OptionService;
import lms.service.QuestionService;
import lms.service.TestService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/test")
@RequiredArgsConstructor
public class TestApi {
    private final TestService testService;
    private final QuestionService questionService;
    private final OptionService optionService;

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
    @PatchMapping ("/save/{testId}")
    public SimpleResponse update (@PathVariable Long testId,
                                  @RequestBody UpdateTestRequest updateTestRequest){
        return testService.update(testId,updateTestRequest);
    }

    @Secured("INSTRUCTOR")
    @Operation(summary = "Начать теста",
            description = "Метод для начала  теста " +
                    " Авторизация: администратор!")
    @PatchMapping("/enableToStart/{testId}")
    public SimpleResponse enableToStart(@PathVariable Long testId){
        return testService.enableToStart(testId);
    }

    @Operation(summary = "Удалить теста",
            description = "Метод для удаления теста по его идентификатору." +
                    " Авторизация: администратор и инструктор!")
    @PreAuthorize("hasAnyAuthority('ADMIN','INSTRUCTOR')")
    @GetMapping("/findById/{testId}")
    public TestResponse findById(@PathVariable Long testId){
        return testService.findById(testId);
    }

    @Operation(summary = "Удалить вопроса",
            description = "Метод для удаления вопроса по его идентификатору." +
                    " Авторизация: инструктор!")
    @PreAuthorize("hasAuthority('INSTRUCTOR')")
    @DeleteMapping("/deleteQuestion/{questionId}")
    public SimpleResponse deleteQuestion(@PathVariable Long questionId){
        return questionService.delete(questionId);
    }

    @Operation(summary = "Удалить варианта",
            description = "Метод для удаления варианта по его идентификатору." +
                    " Авторизация: инструктор!")
    @PreAuthorize("hasAuthority('INSTRUCTOR')")
    @DeleteMapping("/deleteOption/{optionId}")
    public SimpleResponse deleteOption(@PathVariable Long optionId){
        return optionService.deleteOption(optionId);
    }

    @Operation(summary = "Удалить теста",
            description = "Метод для удаления теста по его идентификатору." +
                    " Авторизация: администратор и инструктор!")
    @PreAuthorize("hasAnyAuthority('ADMIN','INSTRUCTOR')")
    @DeleteMapping("/delete/{testId}")
    public SimpleResponse delete(@PathVariable Long testId){
        return testService.delete(testId);
    }
//    delete option
//    delete question
//    findTestById


}
