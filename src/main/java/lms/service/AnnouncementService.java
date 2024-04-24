package lms.service;

import lms.dto.response.AllAnnouncementResponse;
import lms.dto.response.AnnouncementRequest;
import lms.dto.response.AnnouncementResponse;
import lms.dto.response.SimpleResponse;

public interface AnnouncementService {

    SimpleResponse createAnnouncement(AnnouncementRequest announcementRequest);

    SimpleResponse viewAnnouncement(Long announcementId);

    AnnouncementResponse findById(long announcementId);

    AllAnnouncementResponse findAll(int page, int size);

    SimpleResponse isPublished(Long announcementId, boolean isPublished);

    AllAnnouncementResponse findAllAnnouncementByGroupId(int page, int size, long groupId);
}
