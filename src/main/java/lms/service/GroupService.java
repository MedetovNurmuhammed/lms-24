package lms.service;

import lms.dto.request.GroupRequest;
import lms.dto.response.AllGroupResponse;
import lms.dto.response.SimpleResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface GroupService {

    SimpleResponse save(GroupRequest groupRequest);

    SimpleResponse update(long groupId, GroupRequest groupRequest);

    Page<AllGroupResponse> findAllGroup(int size, int page);

    SimpleResponse delete(long groupId);

    List<String> getAllGroupName();
}
