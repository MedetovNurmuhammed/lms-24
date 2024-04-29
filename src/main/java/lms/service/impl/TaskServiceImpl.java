package lms.service.impl;

import com.amazonaws.services.xray.model.Http;
import lms.dto.response.SimpleResponse;
import lms.entities.Task;
import lms.entities.Trash;
import lms.enums.Type;
import lms.exceptions.NotFoundException;
import lms.repository.TaskRepository;
import lms.repository.TrashRepository;
import lms.service.TaskService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaskServiceImpl implements TaskService {
    private final TaskRepository taskRepository;
    private final TrashRepository trashRepository;

    @Override
    public SimpleResponse delete(Long taskId) {
        Task task = taskRepository.findById(taskId).
                orElseThrow(() -> new NotFoundException("Задача не найдена!"));
        Trash trash = new Trash();
        trash.setName(task.getTitle());
        trash.setType(Type.TASK);
        trash.setDateOfDelete(ZonedDateTime.now());
        trash.setTask(task);
        task.setTrash(trash);
        trashRepository.save(trash);
        return SimpleResponse.builder()
                .httpStatus(HttpStatus.OK)
                .message("Успешно добавлено в корзину")
                .build();
    }
}
