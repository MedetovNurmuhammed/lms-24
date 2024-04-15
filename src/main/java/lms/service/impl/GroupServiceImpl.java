package lms.service.impl;

import jakarta.transaction.Transactional;
import lms.dto.request.GroupRequest;
import lms.dto.response.AllGroupResponse;
import lms.dto.response.GroupWithStudentsResponse;
import lms.dto.response.SimpleResponse;
import lms.dto.response.StudentResponse;
import lms.entities.Course;
import lms.entities.Group;
import lms.entities.Student;
import lms.exceptions.AlreadyExistsException;
import lms.repository.GroupRepository;
import lms.service.GroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class GroupServiceImpl implements GroupService {
    private final GroupRepository groupRepository;

    @Override
    public SimpleResponse save(GroupRequest groupRequest) {
        boolean exists = groupRepository.existsByTitle(groupRequest.title());
        if (exists) throw new AlreadyExistsException("Группа с названием " + groupRequest.title() + " уже существует");

        groupRepository.save(
                Group.builder()
                        .image(groupRequest.image())
                        .title(groupRequest.title())
                        .description(groupRequest.description())
                        .dateOfStart(LocalDate.now())
                        .dateOfEnd(groupRequest.dateOfEnd())
                        .build()
        );
        return SimpleResponse.builder()
                .httpStatus(HttpStatus.ACCEPTED)
                .message("Группа успешно создана!")
                .build();
    }

    @Override
    public GroupWithStudentsResponse findById(int size, int page, long id) {
        Group group = groupRepository.getById(id);

        List<StudentResponse> students = new ArrayList<>();
        for (Student student : group.getStudents()) {
            students.add(
                    StudentResponse.builder()
                            .fullName(student.getUser().getFullName())
                            .studyFormat(student.getStudyFormat())
                            .phoneNumber(student.getUser().getPhoneNumber())
                            .email(student.getUser().getEmail())
                            .build()
            );
        }
        Pageable pageable = PageRequest.of(page - 1, size);
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), students.size());
        Page<StudentResponse> studentResponsePage = new PageImpl<>
                (students.subList(start, end), pageable, students.size());

        return GroupWithStudentsResponse.builder()
                .id(group.getId())
                .title(group.getTitle())
                .students(studentResponsePage)
                .build();
    }

    @Override
    public SimpleResponse update(long groupId, GroupRequest groupRequest) {
        Group updatedGroup = groupRepository.getById(groupId);

        if (!updatedGroup.getTitle().equals(groupRequest.title())) {
            boolean exists = groupRepository.existsByTitle(groupRequest.title());
            if (exists)
                throw new AlreadyExistsException("Группа с названием " + groupRequest.title() + " уже существует");
        }
        updatedGroup.setTitle(groupRequest.title());
        updatedGroup.setDescription(groupRequest.description());
        updatedGroup.setImage(groupRequest.image());
        updatedGroup.setDateOfEnd(groupRequest.dateOfEnd());

        return SimpleResponse.builder()
                .httpStatus(HttpStatus.OK)
                .message("Успешно обнавлено")
                .build();
    }

    @Override
    public Page<AllGroupResponse> findAllGroup(int size, int page) {
        Pageable pageable = PageRequest.of(page - 1, size);
        return groupRepository.findAllGroup(pageable);
    }

    @Override
    public SimpleResponse delete(long groupId) {
        Group deletedGroup = groupRepository.getById(groupId);

        List<Course> courses = deletedGroup.getCourses();
        for (Course course : courses) {
            course.setGroups(null);
        }
        deletedGroup.setCourses(null);

        groupRepository.delete(deletedGroup);

        return SimpleResponse.builder()
                .httpStatus(HttpStatus.OK)
                .message("Группа успешно удалена!")
                .build();
    }

    @Override
    public List<String> getAllGroupName() {
        return groupRepository.getAllGroupName();
    }

}
