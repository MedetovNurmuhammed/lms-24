package lms.service;

import lms.dto.request.GroupRequest;
import lms.dto.response.AllGroupResponse;
import lms.dto.response.GroupResponse;
import lms.dto.response.SimpleResponse;

import java.util.List;

public interface GroupService {

    SimpleResponse save(GroupRequest groupRequest);

    SimpleResponse update(long groupId, GroupRequest groupRequest);

    AllGroupResponse findAllGroup(int size, int page);

    SimpleResponse delete(long groupId);

    GroupResponse getGroup(Long groupId);
}
