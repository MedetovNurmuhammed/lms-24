package lms.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lms.dto.request.TaskRequest;
import lms.dto.response.AllTaskResponse;
import lms.dto.response.SimpleResponse;
import lms.dto.response.TaskResponse;
import lms.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskApi {
    private final TaskService taskService;

    @Secured("INSTRUCTOR")
    @Operation(summary = "Создание задания для урока", description = "Создает новое задание для указанного урока.")
    @PostMapping(value = "/create/{lessonId}")
    public SimpleResponse createTask(@PathVariable("lessonId") Long lessonId,
                                     @RequestBody @Valid TaskRequest taskRequest) throws MessagingException {
        return taskService.createTask(lessonId, taskRequest);
    }

    @Secured({"INSTRUCTOR", "STUDENT"})
    @Operation(summary = "Поиск задания по идентификатору", description = "Находит задание по указанному идентификатору.")
    @GetMapping("/findById/{taskId}")
    public TaskResponse findById(@PathVariable("taskId") Long taskId) {
        return taskService.findById(taskId);
    }

    @Secured("INSTRUCTOR")
    @Operation(summary = "Обновление задания по идентификатору", description = "Обновляет существующее задание по указанному идентификатору.")
    @PatchMapping("/update/{taskId}")
    public SimpleResponse update(@PathVariable Long taskId, @RequestBody  @Valid TaskRequest taskRequest) throws MessagingException {
        return taskService.updateTask(taskId, taskRequest);
    }

    @Operation(summary = "Удалить задачку",
            description = "Метод для удаления задачку по его идентификатору." +
                    " Авторизация: администратор и инструктор!")
    @PreAuthorize("hasAnyAuthority('ADMIN','INSTRUCTOR')")
    @DeleteMapping("/delete/{taskId}")
    public SimpleResponse delete(@PathVariable Long taskId){
        return taskService.delete(taskId);
    }


    @Secured({"INSTRUCTOR","STUDENT"})
    @Operation(summary = "Поиск заданий по идентификатору урока", description = "Находит все задания для указанного урока.")
    @GetMapping("/findTaskByLessonId/{lessonId}")
    public AllTaskResponse findTaskByLessonId(@PathVariable("lessonId") Long lessonId,
                                              @RequestParam(required = false,defaultValue = "1") int page,@RequestParam(required = false,defaultValue = "6") int size ) {
        return taskService.findAllTaskByLessonId(page,size,lessonId);
    }

}


