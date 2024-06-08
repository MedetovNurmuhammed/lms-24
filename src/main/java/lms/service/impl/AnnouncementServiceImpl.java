package lms.service.impl;

import jakarta.transaction.Transactional;
import lms.dto.request.AnnouncementRequest;
import lms.dto.response.*;
import lms.entities.*;
import lms.enums.Role;
import lms.exceptions.BadRequestException;
import lms.exceptions.NotFoundException;
import lms.repository.*;
import lms.service.AnnouncementService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class AnnouncementServiceImpl implements AnnouncementService {
    private final AnnouncementRepository announcementRepository;
    private final GroupRepository groupRepository;
    private final UserRepository userRepository;
    private final StudentRepository studentRepository;
    private final InstructorRepository instructorRepository;

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

        List<Group> groups = new ArrayList<>();
        for (Long groupId : announcementRequest.targetGroupIds()) {

            Group group = groupRepository.findById(groupId).orElseThrow(() ->
                    new NotFoundException("Группа не найден!"));
            group.getStudents().forEach(
                    student -> student.getAnnouncements().put(announcement, false)
            );

        }
        announcement.setGroups(groups);

        return SimpleResponse.builder()
                .message("Announcement created")
                .httpStatus(HttpStatus.ACCEPTED)
                .build();
    }

    @Override
    public AnnouncementResponse viewAnnouncement(Long announcementId) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.getByEmail(email);
        Announcement announcement = getById(announcementId);
        if (currentUser.getRole().equals(Role.STUDENT)) {
            Student student = studentRepository.findByUserId(currentUser.getId()).orElseThrow(() -> new NotFoundException("Student not found"));
            student.getAnnouncements().put(announcement, true);
        }
        return AnnouncementResponse.builder()
                .id(announcement.getId())
                .content(announcement.getAnnouncementContent())
                .owner(announcement.getUser().getFullName())
                .isPublished(announcement.getIsPublished())
                .publishDate(announcement.getPublishedDate())
                .endDate(announcement.getExpirationDate())
                .build();
    }


    @Override
    public AllAnnouncementResponse findAllAnnouncementByGroupId(int page, int size, Long groupId) {
        if (page < 1 && size < 1) throw new java.lang.IllegalArgumentException("Индекс страницы не должен быть меньше нуля");
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("id"));
        if (groupId == null) {
            return findAll(page, size);
        }
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new NotFoundException("Группа не найден!"));

        Page<Announcement> announcementPage = announcementRepository.findAllByGroupId(group.getId(), pageable);
        List<AnnouncementResponse> announcementResponses = announcementPage.getContent().stream()
                .map(this::mapToAnnouncementResponse)
                .collect(Collectors.toList());

        return AllAnnouncementResponse.builder()
                .page(announcementPage.getNumber() + 1)
                .size(announcementPage.getNumberOfElements())
                .announcements(announcementResponses)
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
        List<Group> groups = groupRepository.allGroupById(announcementRequest.targetGroupIds());
        announcement.setGroups(groups);
        announcement.setExpirationDate(announcementRequest.expirationDate());
        announcement.setPublishedDate(announcementRequest.publishedDate());
        return SimpleResponse.builder()
                .httpStatus(HttpStatus.OK)
                .message("Успешно обновлено")
                .build();
    }

    public AllAnnouncementOfStudentResponse allAnnouncementOfStudent(int page, int size, Boolean isView) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Student student = studentRepository.getStudentByEmail(email)
                .orElseThrow(() -> new NotFoundException("Студент не найден"));
        if (page < 1 && size < 1) throw new BadRequestException("Page - size  страницы должен быть больше 0.");

        List<AnnouncementOfStudent> announcementsOfStudent = student.getAnnouncements().entrySet().stream()
                .map(entry -> AnnouncementOfStudent.builder()
                        .announcementId(entry.getKey().getId())
                        .content(entry.getKey().getAnnouncementContent())
                        .author(entry.getKey().getUser().getFullName())
                        .isView(entry.getValue())
                        .build())
                .filter(announcementOfStudent -> isView == null || announcementOfStudent.isView().equals(isView))
                .collect(Collectors.toList());

        int start = Math.min((page - 1) * size, announcementsOfStudent.size());
        int end = Math.min(start + size, announcementsOfStudent.size());

        List<AnnouncementOfStudent> pagedAnnouncements = announcementsOfStudent.subList(start, end);

        return AllAnnouncementOfStudentResponse.builder()
                .page(page)
                .size(pagedAnnouncements.size())
                .announcements(pagedAnnouncements)
                .build();
    }

    private AllAnnouncementResponse findAll(int page, int size) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.getByEmail(email);
        Pageable pageable = getPageable(page, size);
        Page<Announcement> announcementPage;

        if (currentUser.getRole() == Role.ADMIN) {
            announcementPage = announcementRepository.findAll(pageable);
        } else if (currentUser.getRole() == Role.INSTRUCTOR) {
            Instructor instructor = instructorRepository.findByUserId(currentUser.getId()).orElseThrow(() ->
                    new NotFoundException("Instructor with id:" + currentUser.getId() + " not found"));
            List<Long> groupsIds = groupRepository.findAllByInstructorId(instructor.getId());
            announcementPage = announcementRepository.findAllInstructorAnnouncement(groupsIds, pageable);
        } else {
            throw new IllegalStateException("Неподдерживаемая роль пользователя");
        }

        List<AnnouncementResponse> announcementResponses = announcementPage.getContent().stream()
                .map(this::mapToAnnouncementResponse)
                .collect(Collectors.toList());

        return AllAnnouncementResponse.builder()
                .page(announcementPage.getNumber() + 1)
                .size(announcementPage.getNumberOfElements())
                .announcements(announcementResponses)
                .build();
    }

    private AnnouncementResponse mapToAnnouncementResponse(Announcement announcement) {
        return AnnouncementResponse.builder()
                .id(announcement.getId())
                .content(announcement.getAnnouncementContent())
                .owner(announcement.getUser().getFullName())
                .groupNames(announcement.getGroups().stream()
                        .map(Group::getTitle)
                        .collect(Collectors.toList()))
                .isPublished(announcement.getIsPublished())
                .publishDate(announcement.getPublishedDate())
                .endDate(announcement.getExpirationDate())
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

    private Pageable getPageable(int page, int size) {
        if (page < 1 && size < 1) throw new BadRequestException("Page - size  страницы должен быть больше 0.");
        return PageRequest.of(page - 1, size, Sort.by("publishedDate").descending());
    }
}
