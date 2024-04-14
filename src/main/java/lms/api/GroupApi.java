package lms.api;

import jakarta.validation.Valid;
import lms.dto.request.GroupRequest;
import lms.dto.response.AllGroupResponse;
import lms.dto.response.GroupResponse;
import lms.dto.response.SimpleResponse;
import lms.service.GroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/group")
public class GroupApi {
    private final GroupService groupService;

    @Secured("ADMIN")
    @PostMapping("/save")
    public SimpleResponse save(@RequestBody @Valid GroupRequest groupRequest) {
        return groupService.save(groupRequest);
    }

    @Secured("ADMIN")
    @GetMapping("/findById/{groupId}")
    public GroupResponse findById(@PathVariable long groupId,@RequestParam int page, @RequestParam int size) {
      return groupService.findById(size,page,groupId);
    }

    @Secured("ADMIN")
    @PutMapping("/update/{groupId}")
    public SimpleResponse update(@PathVariable long groupId, @RequestBody @Valid GroupRequest groupRequest) {
        return groupService.update(groupId,groupRequest);
    }

    @Secured("ADMIN")
    @GetMapping("/findAll")
    public Page<AllGroupResponse> findAll(@RequestParam int page, @RequestParam int size) {
        return groupService.findAllGroup(size,page);
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
