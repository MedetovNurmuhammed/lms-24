package lms.service;

import jakarta.validation.Valid;
import lms.dto.request.AnnouncementRequest;
import lms.dto.response.AllAnnouncementOfStudentResponse;
import lms.dto.response.AllAnnouncementResponse;
import lms.dto.response.AnnouncementResponse;
import lms.dto.response.SimpleResponse;

public interface AnnouncementService {

    SimpleResponse createAnnouncement(AnnouncementRequest announcementRequest);

    AnnouncementResponse viewAnnouncement(Long announcementId);

    SimpleResponse isPublished(Long announcementId, boolean isPublished);

    AllAnnouncementResponse findAllAnnouncementByGroupId(int page, int size, Long groupId);

    SimpleResponse deleteById(long announcementId);

    SimpleResponse update(long announcementId, @Valid AnnouncementRequest announcementRequest);

    AllAnnouncementOfStudentResponse allAnnouncementOfStudent(int page, int size, Boolean isView);
}
