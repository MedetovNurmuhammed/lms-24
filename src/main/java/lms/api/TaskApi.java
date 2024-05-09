package lms.api;

import io.swagger.v3.oas.annotations.Operation;
import lms.dto.response.SimpleResponse;
import lms.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskApi {
    private final TaskService taskService;


    @Operation(summary = "Удалить задачку",
            description = "Метод для удаления задачку по его идентификатору." +
                    " Авторизация: администратор и инструктор!")
    @PreAuthorize("hasAnyAuthority('ADMIN','INSTRUCTOR')")
    @DeleteMapping("/delete/{taskId}")
    public SimpleResponse delete(@PathVariable Long taskId){
        return taskService.delete(taskId);
    }
}
