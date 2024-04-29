package lms.service;
import lms.dto.response.SimpleResponse;

public interface LinkService {
    SimpleResponse delete(Long linkId);
}
