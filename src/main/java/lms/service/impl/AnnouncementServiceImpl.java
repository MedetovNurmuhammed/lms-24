package lms.service.impl;

import jakarta.transaction.Transactional;
import lms.dto.response.AllAnnouncementResponse;
import lms.dto.response.AnnouncementRequest;
import lms.dto.response.AnnouncementResponse;
import lms.dto.response.SimpleResponse;
import lms.entities.Announcement;
import lms.entities.Group;
import lms.entities.Student;
import lms.entities.User;
import lms.enums.Role;
import lms.exceptions.ForbiddenException;
import lms.repository.AnnouncementRepository;
import lms.repository.GroupRepository;
import lms.repository.StudentRepository;
import lms.repository.UserRepository;
import lms.service.AnnouncementService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Transactional
public class AnnouncementServiceImpl implements AnnouncementService {
    private final AnnouncementRepository announcementRepository;
    private final GroupRepository groupRepository;
    private final UserRepository userRepository;
    private final StudentRepository studentRepository;

    @Override
    public SimpleResponse createAnnouncement(AnnouncementRequest announcementRequest) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.getByEmail(email);
        Announcement announcement = new Announcement();

        announcement.setIsPublished(false);
        announcement.setPublishedDate(announcementRequest.publishedDate());
        announcement.setAnnouncementContent(announcementRequest.announcementContent());
        announcement.setExpirationDate(announcementRequest.expirationDate());
        announcement.setUser(currentUser);
        announcementRepository.save(announcement);

        List<Group> groups = groupRepository.findAllById(announcementRequest.targetGroupIds());
        for (Group group : groups) {
            for (Student student : group.getStudents()) {
                student.getAnnouncements().put(announcement, false);
            }
        }
        return SimpleResponse.builder()
                .message("Announcement created")
                .httpStatus(HttpStatus.ACCEPTED)
                .build();
    }

    @Override
    public SimpleResponse viewAnnouncement(Long announcementId) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.getByEmail(email);
        if (!currentUser.getRole().equals(Role.STUDENT)) throw new ForbiddenException("Forbidden 403");

        Student student = studentRepository.findByUserId(currentUser.getId());
        Announcement announcement = announcementRepository.findById(announcementId).orElseThrow(() -> new NoSuchElementException("announcement not found"));
        student.getAnnouncements().put(announcement, true);
        return SimpleResponse.builder()
                .httpStatus(HttpStatus.OK)
                .message("Announcement viewed")
                .build();
    }

    @Override
    public AnnouncementResponse findById(long announcementId) {
        Announcement announcementNotFound = announcementRepository.getById(announcementId);
        return AnnouncementResponse.builder()
                .id(announcementNotFound.getId())
                .content(announcementNotFound.getAnnouncementContent())
                .publishDate(announcementNotFound.getPublishedDate())
                .endDate(announcementNotFound.getExpirationDate())
                .build();
    }

    @Override
    public AllAnnouncementResponse findAll(int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.getByEmail(email);

        if (currentUser.getRole().equals(Role.STUDENT)) {
//            Page<AnnouncementResponse> announcementResponses = announcementRepository.findAll(pageable);
        }

        return null;
    }

    @Override
    public SimpleResponse isPublished(Long announcementId, boolean isPublished) {
        Announcement announcement = announcementRepository.getById(announcementId);
        announcement.setIsPublished(isPublished);
        return SimpleResponse.builder()
                .httpStatus(HttpStatus.OK)
                .message("Announcement isPublished")
                .build();
    }

    @Override
    public AllAnnouncementResponse findAllAnnouncementByGroupId(int page, int size,long groupId) {
        Pageable pageable = PageRequest.of(page - 1, size);

        Group group = groupRepository.getById(groupId);
        Page<AnnouncementResponse> announcementResponses =  announcementRepository.findAllByGroupId(group.getId(),pageable);
        return null;
    }


}
