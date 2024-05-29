package lms.api;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lms.dto.request.TaskRequest;
import lms.dto.response.AllTaskResponse;
import lms.dto.response.SimpleResponse;
import lms.dto.response.TaskResponse;
import lms.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
@CrossOrigin(origins = "*",maxAge = 3600)
public class TaskApi {
    private final TaskService taskService;

    @Secured("INSTRUCTOR")
    @Operation(summary = "Создание задания для урока", description = "Создает новое задание для указанного урока.")
    @PostMapping("/{lessonId}")
    public SimpleResponse createTask(@PathVariable("lessonId") Long lessonId,
                                     @RequestBody @Valid TaskRequest taskRequest) throws MessagingException {
        return taskService.createTask(lessonId, taskRequest);
    }

    @Secured({"INSTRUCTOR", "STUDENT"})
    @Operation(summary = "Поиск задания по идентификатору", description = "Находит задание по указанному идентификатору.")
    @GetMapping("/{taskId}")
    public TaskResponse findById(@PathVariable("taskId") Long taskId) {
        return taskService.findById(taskId);
    }

    @Secured("INSTRUCTOR")
    @Operation(summary = "Обновление задания по идентификатору", description = "Обновляет существующее задание по указанному идентификатору.")
    @PatchMapping("/{taskId}")
    public SimpleResponse update(@PathVariable Long taskId, @RequestBody  @Valid TaskRequest taskRequest) throws MessagingException {
        return taskService.updateTask(taskId, taskRequest);
    }

    @Secured("INSTRUCTOR")
    @Operation(summary = "Удаление задания", description = "Удаляет задание по указанному идентификатору.")
    @DeleteMapping("/{taskId}")
    public SimpleResponse delete(@PathVariable("taskId") Long taskId) {
        return taskService.deleteTask(taskId);
    }

    @Secured({"INSTRUCTOR","STUDENT"})
    @Operation(summary = "Поиск заданий по идентификатору урока", description = "Находит все задания для указанного урока.")
    @GetMapping("/taskOfLesson/{lessonId}")
    public AllTaskResponse findTaskByLessonId(@PathVariable("lessonId") Long lessonId) {
        return taskService.findAllTaskByLessonId(lessonId);
    }

}


