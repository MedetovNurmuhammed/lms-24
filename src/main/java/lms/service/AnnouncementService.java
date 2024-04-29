package lms.service;

import jakarta.validation.Valid;
import lms.dto.response.*;

public interface AnnouncementService {

    SimpleResponse createAnnouncement(AnnouncementRequest announcementRequest);

    AnnouncementResponse viewAnnouncement(Long announcementId);

    AllAnnouncementResponse findAll(int page, int size);

    SimpleResponse isPublished(Long announcementId, boolean isPublished);

    AllAnnouncementResponse findAllAnnouncementByGroupId(int page, int size, Long groupId);

    SimpleResponse deleteById(long announcementId);

    SimpleResponse update(long announcementId, @Valid AnnouncementRequest announcementRequest);

    AllAnnouncementOfStudentResponse allAnnouncementOfStudent(int page,int size,Boolean isView);
}
