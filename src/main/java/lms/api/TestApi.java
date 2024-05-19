package lms.api;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lms.dto.request.TestRequest;
import lms.dto.request.UpdateTestRequest;
import lms.dto.response.AllTestResponse;
import lms.dto.response.SimpleResponse;
import lms.dto.response.TestResponse;
import lms.dto.response.TestResponseWithStudents;
import lms.service.OptionService;
import lms.service.QuestionService;
import lms.service.TestService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.DeleteMapping;

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
    @PatchMapping ("/update/{testId}")
    public SimpleResponse update (@PathVariable Long testId,
                                  @RequestBody UpdateTestRequest updateTestRequest){
        return testService.update(testId,updateTestRequest);
    }

    @Secured("INSTRUCTOR")
    @Operation(summary = "Доступ к тесту",
            description = "Метод для доступ к тесту." +
                    " Авторизация: администратор!")
    @PatchMapping("/enableToStart/{testId}")
    public SimpleResponse accessToTest(@PathVariable Long testId){
        return testService.accessToTest(testId);
    }

    @Operation(summary = "Найти теста",
            description = "Метод для найти теста по его идентификатору." +
                    " Авторизация: инструктор!")
    @PreAuthorize("hasAuthority('INSTRUCTOR')")
    @GetMapping("/findById/{testId}")
    public TestResponseWithStudents findById(@PathVariable Long testId){
        return testService.findById(testId);
    }

    @Operation(summary = "Найти теста для редактирования",
            description = "Метод для найти теста по его идентификатору." +
                    " Авторизация: инструктор!")
    @PreAuthorize("hasAuthority('INSTRUCTOR')")
    @GetMapping("/findByIdForEdit/{testId}")
    public TestResponse findTestByIForEdit(@PathVariable Long testId){
        return testService.findTestByIdForEdit(testId);
    }

    @Operation(summary = "Найти все тесты ",
            description = "Метод для найти всех тестов." +
                    " Авторизация: инструктор!")
    @PreAuthorize("hasAuthority('INSTRUCTOR')")
    @GetMapping("/findAll/{lessonId}")
     public AllTestResponse findAll(@PathVariable Long lessonId){
       return testService.findAll(lessonId);
    }

    @Operation(summary = "Удалить вопроса",
            description = "Метод для удаления вопроса по его идентификатору." +
                    " Авторизация: инструктор!")
    @PreAuthorize("hasAuthority('INSTRUCTOR')")
    @DeleteMapping("/deleteQuestion/{questionId}")
    public SimpleResponse deleteQuestion(@PathVariable Long questionId){
        return questionService.delete(questionId);
    }

    @Operation(summary = "Удалить вариант-ответа",
            description = "Метод для удаления варианта по его идентификатору." +
                    " Авторизация: инструктор!")
    @PreAuthorize("hasAuthority('INSTRUCTOR')")
    @DeleteMapping("/deleteOption/{optionId}")
    public SimpleResponse deleteOption(@PathVariable Long optionId){
        return optionService.deleteOption(optionId);
    }

    @Operation(summary = "Удалить теста (добавление в корзину.)",
            description = "Метод для удаления теста по его идентификатору." +
                    " Авторизация: инструктор!")
    @PreAuthorize("hasAuthority('INSTRUCTOR')")
    @DeleteMapping("/delete/{testId}")
    public SimpleResponse delete(@PathVariable Long testId){
        return testService.delete(testId);
    }
}
