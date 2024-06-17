package lms.service;

import lms.dto.request.EditPresentationRequest;
import lms.dto.response.SimpleResponse;
import lms.dto.request.PresentationRequest;
import lms.dto.response.PresentationResponse;


import java.util.List;

public interface PresentationService {
    SimpleResponse createPresentation(Long lessonId, PresentationRequest presentationRequest);

    SimpleResponse editPresentation(Long presentationId, EditPresentationRequest presentationRequest);

    PresentationResponse findById(Long presentationId);

    SimpleResponse deletePresentationById(Long presentationId);

    List<PresentationResponse> findAllPresentationByLessonId(Long lessonId);
}
