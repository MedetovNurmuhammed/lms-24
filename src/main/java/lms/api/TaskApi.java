package lms.api;

import jakarta.mail.MessagingException;
import lms.dto.request.TaskRequest;
import lms.dto.response.SimpleResponse;
import lms.dto.response.TaskResponse;
import lms.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskApi {
    private final TaskService taskService;

    @Secured("INSTRUCTOR")
    @PostMapping(value = "/create/{lessonId}")
    public SimpleResponse createTask(@PathVariable("lessonId") Long lessonId,
                                     @RequestBody TaskRequest taskRequest) throws MessagingException {
        return taskService.createTask(lessonId, taskRequest);
    }

    @Secured({"INSTRUCTOR", "STUDENT"})
    @GetMapping("/findById/{taskId}")
    public TaskResponse findById(@PathVariable("taskId") Long taskId) {
        return taskService.findById(taskId);
    }

    @Secured("INSTRUCTOR")
    @PatchMapping("/update/{taskId}")
    public SimpleResponse update(@PathVariable Long taskId, @RequestBody TaskRequest taskRequest) {
        return taskService.updateTask(taskId, taskRequest);
    }

    @Secured("INSTRUCTOR")
    @DeleteMapping("/delete/{takId}")
    public SimpleResponse delete(@PathVariable("takId") Long takId) {
        return taskService.deleteTask(takId);
    }
}


