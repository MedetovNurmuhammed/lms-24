package lms.api;

import jakarta.validation.Valid;
import lms.dto.request.GroupRequest;
import lms.dto.response.AllGroupResponse;
import lms.dto.response.SimpleResponse;
import lms.service.GroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/group")
@CrossOrigin(origins = "*",maxAge = 3600)
public class GroupApi {
    private final GroupService groupService;

    @Secured("ADMIN")
    @PostMapping("/Создать группу")
    public SimpleResponse save(@RequestBody @Valid GroupRequest groupRequest) {
        return groupService.save(groupRequest);
    }

    @Secured("ADMIN")
    @PutMapping("/update/{groupId}")
    public SimpleResponse update(@PathVariable long groupId, @RequestBody @Valid GroupRequest groupRequest) {
        return groupService.update(groupId, groupRequest);
    }

    @Secured("ADMIN")
    @GetMapping("/findAll")
    public Page<AllGroupResponse> findAll(@RequestParam int page, @RequestParam int size) {
        return groupService.findAllGroup(size, page);
    }

    @Secured("ADMIN")
    @DeleteMapping("/delete/{groupId}")
    public SimpleResponse delete(@PathVariable long groupId) {
        return groupService.delete(groupId);
    }

    @Secured("ADMIN")
    @GetMapping("/getAllGroupName")
    public List<String> getAllGroupName() {
        return groupService.getAllGroupName();
    }
}
