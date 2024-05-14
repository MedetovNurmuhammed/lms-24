package lms.api;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.mail.MessagingException;
import lms.dto.request.CheckAnswerRequest;
import lms.dto.response.FilterAnswerOfTaskResponse;
import lms.dto.response.AnswerTaskResponse;
import lms.dto.response.SimpleResponse;
import lms.enums.TaskAnswerStatus;
import lms.service.AnswerTaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/resultTask")
@RequiredArgsConstructor
public class ResultTaskInstructorApi {

    private final AnswerTaskService answerTaskService;

    @Secured("INSTRUCTOR")
    @Operation(summary = "Получение ответа на задание по идентификатору ответа",
            description = "Возвращает ответ на задание для указанного идентификатора ответа." +
                    " Авторизация: инструктор!")
    @GetMapping("/getAnswerById/{answerId}")
    public AnswerTaskResponse getAnswerById(@PathVariable("answerId") Long answerId)  {
        return answerTaskService.getAnswerById(answerId);
    }

    @Secured("INSTRUCTOR")
    @Operation(summary = "Поиск всех ответов на задание", description = "Возвращает список ответов на задание," +
            " отфильтрованных по идентификатору задания и статусу ответа. Авторизация: инструктор!")
    @GetMapping("/findAll/{taskId}")
    public List<FilterAnswerOfTaskResponse> findAll(@PathVariable("taskId") Long taskId,
                                                    @RequestParam (defaultValue = "WAITING" )TaskAnswerStatus answerStatus){
        return answerTaskService.filterAnswerTask(taskId,answerStatus);
    }

    @Secured("INSTRUCTOR")
    @Operation(summary = "Получить список студентов, не отправивших ответ на задание",
            description = "Возвращает список имен студентов, которые не предоставили ответ на указанное задание. Авторизация: инструктор!")
    @GetMapping("notAnswered/{taskId}")
    public List<String> notAnswered(@PathVariable("taskId") Long taskId){
        return answerTaskService.getNotAnswered(taskId);
    }

    @Secured("INSTRUCTOR")
    @Operation(summary = "Проверить ответ студента",
            description = "Проверяет и оценивает ответ, предоставленный студентом на задание. Авторизация: инструктор!")
    @PatchMapping("/checkAnswer/{answerId}")
    public SimpleResponse checkAnswer(@PathVariable Long answerId, @RequestBody CheckAnswerRequest checkAnswerRequest) throws MessagingException {
        return answerTaskService.checkAnswer(answerId,checkAnswerRequest);
    }

}
