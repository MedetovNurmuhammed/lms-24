package lms.service;

import lms.dto.response.SimpleResponse;
import lms.dto.request.EditPresentationRequest;
import lms.dto.request.PresentationRequest;
import lms.dto.response.FindAllPresentationResponse;
import lms.dto.response.PresentationResponse;
import lms.dto.response.SimpleResponse;
import org.springframework.web.multipart.MultipartFile;


public interface PresentationService {
    SimpleResponse createPresentation(Long lessonId, PresentationRequest presentationRequest);

    SimpleResponse editPresentation(Long presentationId, EditPresentationRequest presentationRequest);

    PresentationResponse findById(Long presentationId);

    SimpleResponse deletePresentationById(Long presentationId);

    FindAllPresentationResponse findAllPresentationByLessonId(int page, int size, Long lessonId);

}
