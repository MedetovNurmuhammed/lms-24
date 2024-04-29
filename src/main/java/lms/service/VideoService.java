package lms.service;

import lms.dto.response.SimpleResponse;

public interface VideoService {
    SimpleResponse delete(Long studId);
}
