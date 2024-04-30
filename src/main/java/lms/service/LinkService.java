package lms.service;

import lms.dto.request.LinkRequest;
import lms.dto.response.AllLinkResponse;
import lms.dto.response.LinkResponse;
import lms.dto.response.SimpleResponse;

public interface LinkService {
    SimpleResponse addLink(LinkRequest linkRequest, Long lessonId);

    AllLinkResponse findAll(int page, int size, Long lessonId);

    LinkResponse findById(Long linkId);

    SimpleResponse update(LinkRequest linkRequest, Long linkId);

    SimpleResponse delete(Long linkId);
}
