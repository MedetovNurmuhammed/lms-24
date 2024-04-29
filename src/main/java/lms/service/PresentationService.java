package lms.service;

import lms.dto.response.SimpleResponse;

public interface PresentationService {
    SimpleResponse delete(Long presentationId);
}
