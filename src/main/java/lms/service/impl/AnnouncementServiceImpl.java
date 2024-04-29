package lms.service.impl;

import jakarta.transaction.Transactional;
import lms.dto.response.*;
import lms.entities.*;
import lms.exceptions.NotFoundException;
import lms.repository.AnnouncementRepository;
import lms.repository.GroupRepository;
import lms.repository.StudentRepository;
import lms.repository.UserRepository;
import lms.service.AnnouncementService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
        if (announcementRequest.expirationDate().isBefore(announcementRequest.publishedDate()))
            throw new IllegalArgumentException("Дата окончания должна быть после даты публикации!");

        Announcement announcement = new Announcement();

        announcement.setIsPublished(false);
        announcement.setPublishedDate(announcementRequest.publishedDate());
        announcement.setAnnouncementContent(announcementRequest.announcementContent());
        announcement.setExpirationDate(announcementRequest.expirationDate());
        announcement.setUser(currentUser);
        announcementRepository.save(announcement);

        List<Group> groups = groupRepository.findAllById(announcementRequest.targetGroupIds());
        announcement.setGroups(groups);
        return SimpleResponse.builder()
                .message("Announcement created")
                .httpStatus(HttpStatus.ACCEPTED)
                .build();
    }

    @Override
    public SimpleResponse viewAnnouncement(Long announcementId) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.getByEmail(email);

        Student student = studentRepository.findByUserId(currentUser.getId()).orElseThrow(() -> new NotFoundException("Student not found"));
        Announcement announcement = announcementRepository.findById(announcementId).orElseThrow(() -> new NoSuchElementException("announcement not found"));
        student.getAnnouncements().put(announcement, true);
        return SimpleResponse.builder()
                .httpStatus(HttpStatus.OK)
                .message("Объявление просмотрено")
                .build();
    }

    @Override
    public AnnouncementResponse findById(long announcementId) {
        Announcement announcement = announcementRepository.getById(announcementId);
        return AnnouncementResponse.builder()
                .id(announcement.getId())
                .content(announcement.getAnnouncementContent())
                .isPublished(announcement.getIsPublished())
                .publishDate(announcement.getPublishedDate())
                .endDate(announcement.getExpirationDate())
                .build();
    }

    @Override
    public AllAnnouncementResponse findAll(int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("publishedDate").descending());
        Page<AnnouncementResponse> announcementResponses = announcementRepository.findAllAnnouncement(pageable);
        return AllAnnouncementResponse.builder()
                .page(announcementResponses.getNumber() + 1)
                .size(announcementResponses.getNumberOfElements())
                .announcements(announcementResponses.getContent())
                .build();
    }

    @Override
    public SimpleResponse isPublished(Long announcementId, boolean isPublished) {
        Announcement announcement = getById(announcementId);
        if (isPublished) {
            announcement.setIsPublished(true);
            for (Group group : announcement.getGroups()) {
                for (Student student : group.getStudents()) {
                    student.getAnnouncements().put(announcement, false);
                }
            }
        } else {
            announcement.setIsPublished(false);
            announcementRepository.deleteByAnnouncementIdNative(announcementId);
        }
        return SimpleResponse.builder()
                .httpStatus(HttpStatus.OK)
                .message("Announcement isPublished")
                .build();
    }

    @Override
    public AllAnnouncementResponse findAllAnnouncementByGroupId(int page, int size, Long groupId) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("publishedDate").descending());
        if (groupId == null) {
            return findAll(page, size);
        }
        Group group = groupRepository.getById(groupId);
        Page<AnnouncementResponse> announcementResponses = announcementRepository.findAllByGroupId(group.getId(), pageable);
        return AllAnnouncementResponse.builder()
                .page(announcementResponses.getNumber() + 1)
                .size(announcementResponses.getNumberOfElements())
                .announcements(announcementResponses.getContent())
                .build();
    }

    @Override
    public SimpleResponse deleteById(long announcementId) {
        Announcement announcement = getById(announcementId);
        announcementRepository.deleteByAnnouncementIdNative(announcementId);
        announcementRepository.deleteById(announcement.getId());
        return SimpleResponse.builder()
                .httpStatus(HttpStatus.OK)
                .message("Успешно удалено")
                .build();
    }

    @Override
    public SimpleResponse update(long announcementId, AnnouncementRequest announcementRequest) {
        Announcement announcement = getById(announcementId);
        if (announcementRequest.expirationDate().isBefore(announcementRequest.publishedDate()))
            throw new IllegalArgumentException("Дата окончания должна быть после даты публикации!");

        announcement.setAnnouncementContent(announcementRequest.announcementContent());
        List<Group> groups = groupRepository.findAllById(announcementRequest.targetGroupIds());
        announcement.setGroups(groups);
        announcement.setExpirationDate(announcementRequest.expirationDate());
        announcement.setPublishedDate(announcementRequest.publishedDate());
        return SimpleResponse.builder()
                .httpStatus(HttpStatus.OK)
                .message("Успешно обновлено")
                .build();
    }

    @Override
    public AllAnnouncementOfStudentResponse allAnnouncementOfStudent(Boolean isView) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Student student = studentRepository.getStudentByEmail(email).orElseThrow(() -> new NotFoundException("Студент не найден"));
        Map<Announcement, Boolean> announcements = student.getAnnouncements();
        List<AnnouncementOfStudent> announcementOfStudents = new ArrayList<>();
        announcements.forEach((announcement, aBoolean) -> {
            announcementOfStudents.add(
                    AnnouncementOfStudent.builder()
                            .announcementId(announcement.getId())
                            .content(announcement.getAnnouncementContent())
                            .isView(aBoolean)
                            .build()
            );
        });
        List<AnnouncementOfStudent> announcementsIsView = announcementOfStudents.stream().filter(announcementOfStudent -> announcementOfStudent.isView().equals(isView)).toList();
        return AllAnnouncementOfStudentResponse.builder()
                .announcements(announcementsIsView)
                .build();
    }

    @Transactional
    @Scheduled(cron = "0 1 12 * * *")
    public void cleanupExpiredAnnouncements() {
        LocalDate today = LocalDate.now();
        List<Announcement> expiredAnnouncements = announcementRepository.findByExpirationDateBefore(today);

        for (Announcement expired : expiredAnnouncements) {
            announcementRepository.deleteByAnnouncementIdNative(expired.getId());
            announcementRepository.delete(expired);
            System.out.println("Удалено просроченное объявление с ID: " + expired.getId());
        }
    }


    public Announcement getById(long id) {
        return announcementRepository.findById(id).orElseThrow(() -> new NoSuchElementException("Объявление с идентификатором " + id + " не найдено"));
    }
}
