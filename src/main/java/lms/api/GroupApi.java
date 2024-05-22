package lms.api;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lms.dto.request.GroupRequest;
import lms.dto.response.AllGroupResponse;
import lms.dto.response.GroupResponse;
import lms.dto.response.SimpleResponse;
import lms.service.GroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/groups")
public class GroupApi {
    private final GroupService groupService;

    @Secured("ADMIN")
    @Operation(summary = "Сохранить группу", description = "Создание новой группы с предоставленными данными.Авторизация: Админ")
    @PostMapping()
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
