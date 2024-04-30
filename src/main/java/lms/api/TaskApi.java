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


}
