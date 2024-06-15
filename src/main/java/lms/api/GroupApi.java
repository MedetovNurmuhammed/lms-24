package lms.api;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lms.dto.request.GroupRequest;
import lms.dto.response.AllGroupResponse;
import lms.dto.response.GroupWithoutPagination;
import lms.dto.response.GroupResponse;
import lms.dto.response.SimpleResponse;
import lms.service.GroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/groups")
@CrossOrigin(origins = "*",maxAge = 3600)
public class GroupApi {
    private final GroupService groupService;

    @Secured("ADMIN")
    @Operation(summary = "Сохранить группу", description = "Создание новой группы с предоставленными данными.Авторизация: Админ")
    @PostMapping
    public SimpleResponse save(@RequestBody @Valid GroupRequest groupRequest) {
        return groupService.save(groupRequest);
    }

    @Secured("ADMIN")
    @Operation(summary = "Обновить группу", description = "Обновление данных существующей группы по ID.Авторизация: Админ")
    @PatchMapping("/{groupId}")
    public SimpleResponse update(@PathVariable Long groupId, @RequestBody  GroupRequest groupRequest) {
        return groupService.update(groupId, groupRequest);
    }

    @Secured("ADMIN")
    @Operation(summary = "Получить все группы", description = "Получение списка всех групп с возможностью пагинации.Авторизация: Админ")
    @GetMapping()
    public AllGroupResponse findAll(@RequestParam(required = false, defaultValue = "1") int page,
                                    @RequestParam(required = false, defaultValue = "8") int size) {
        return groupService.findAllGroup(size, page);
    }

    @Secured({"ADMIN","INSTRUCTOR"})
    @Operation(summary = "Получить все группы без пагинации.", description = "Получение списка всех групп без пагинации.Авторизация: Админ")
    @GetMapping("/getAll")
    public List<GroupWithoutPagination> getAllGroupWithoutPagination(){
        return groupService.getAll();
    }


    @Secured("ADMIN")
    @Operation(summary = "Получить информацию о группе", description = "Получение данных группы по ID. Авторизация: Админ")
    @GetMapping("/{groupId}")
    public GroupResponse get(@PathVariable Long groupId) {
        return groupService.getGroup(groupId);
    }

    @Secured("ADMIN")
    @Operation(summary = "Удалить группу", description = "Удаление группы по ID.Авторизация: Админ")
    @DeleteMapping("/{groupId}")
    public SimpleResponse delete(@PathVariable long groupId) {
        return groupService.delete(groupId);
    }

}
