package lms.api;

import lms.dto.response.AllAnnouncementResponse;
import lms.dto.response.AnnouncementRequest;
import lms.dto.response.AnnouncementResponse;
import lms.dto.response.SimpleResponse;
import lms.service.AnnouncementService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/announcement")
@RequiredArgsConstructor
public class AnnouncementApi {
    private final AnnouncementService announcementService;

    @Secured({"ADMIN", "INSTRUCTOR"})
    @PostMapping("/create")
    public SimpleResponse create(@RequestBody AnnouncementRequest announcementRequest) {
        return announcementService.createAnnouncement(announcementRequest);
    }

    @Secured("STUDENT")
    @PutMapping("/viewAnnouncementById/{announcementId}")
    public SimpleResponse viewAnnouncementById(@PathVariable("announcementId") long announcementId) {
        return announcementService.viewAnnouncement(announcementId);
    }

    @Secured({"ADMIN", "INSTRUCTOR", "STUDENT"})
    @GetMapping("/findById/{announcementId}")
    public AnnouncementResponse findById(@PathVariable("announcementId") long announcementId) {
        return announcementService.findById(announcementId);
    }

    @Secured({"ADMIN", "INSTRUCTOR", "STUDENT"})
    @GetMapping("/findAllAnnouncement")
    public AllAnnouncementResponse findAllAnnouncement(
            @RequestParam(required = false,defaultValue = "1") int page,
            @RequestParam(required = false,defaultValue = "4") int size
    ) {
       return announcementService.findAll(page,size);
    }

    @Secured({"ADMIN","INSTRUCTOR"})
    @PutMapping("/isPublished/{announcementId}")
    public SimpleResponse isPublished(@PathVariable Long announcementId, @RequestParam boolean isPublished) {
        return announcementService.isPublished(announcementId,isPublished);
    }

    @Secured({"ADMIN","INSTRUCTOR"})
    @GetMapping("/findAllAnnouncementByGroupId/{groupId}")
    public AllAnnouncementResponse findAllAnnouncementByGroupId(
            @RequestParam(required = false,defaultValue = "1") int page,
            @RequestParam(required = false,defaultValue = "4") int size,
            @PathVariable long groupId) {
        return announcementService.findAllAnnouncementByGroupId(size,page,groupId);
    }
}
