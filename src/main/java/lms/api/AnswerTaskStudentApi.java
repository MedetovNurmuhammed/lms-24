package lms.api;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.mail.MessagingException;
import lms.dto.request.AnswerTaskRequest;
import lms.dto.response.AnswerTaskResponse;
import lms.dto.response.SimpleResponse;
import lms.service.AnswerTaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/answer")
@RequiredArgsConstructor
@CrossOrigin(origins = "*",maxAge = 3600)
public class AnswerTaskStudentApi {

    private final AnswerTaskService answerTaskService;

    @Secured("STUDENT")
    @Operation(summary = "Сохранение ответа на задание",
            description = "Сохраняет ответ на задание для указанного идентификатора задания.  Авторизация: студент!")
    @PostMapping("/send-answer/{taskId}")
    public SimpleResponse save(@PathVariable("taskId") Long taskId, @RequestBody  AnswerTaskRequest answerTaskRequest) throws MessagingException {
        return answerTaskService.save(taskId,answerTaskRequest);
    }

    @Secured("STUDENT")
    @Operation(summary = "Обновление ответа на задание",
            description = "Обновляет существующий ответ на задание по идентификатору ответа. Авторизация: студент!")
    @PatchMapping("/update/{answerTaskId}")
    public SimpleResponse update(@PathVariable Long answerTaskId, @RequestBody AnswerTaskRequest answerTaskRequest) throws MessagingException {
        return answerTaskService.update(answerTaskId,answerTaskRequest);
    }

    @Secured("STUDENT")
    @Operation(summary = "Поиск ответа на задание по идентификатору задания",
            description = "Возвращает ответ на задание для указанного идентификатора задания." +
            " Авторизация: студент!")
    @GetMapping("/findAnswerByTaskId/{taskId}")
    public AnswerTaskResponse findAnswerByTaskId(@PathVariable("taskId") Long taskId)  {
       return answerTaskService.findAnswerByTaskId(taskId);
    }
}
