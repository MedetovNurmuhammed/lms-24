package lms.api;

import lms.dto.response.SimpleResponse;
import lms.dto.response.TaskRequest;
import lms.dto.response.TaskResponse;
import lms.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskApi {
    private final TaskService taskService;

//    @PostMapping("/create/{lessonId}")
//    public SimpleResponse createTask(@PathVariable("lessonId") Long lessonId, @RequestBody TaskRequest taskRequest) {
//        return taskService.createTask(lessonId,taskRequest);
//    }
//
//    @GetMapping("/findById/{taskId}")
//    public TaskResponse findById(@PathVariable("taskId") Long taskId) {}
}
