package lms.service.impl;

import jakarta.transaction.Transactional;
import lms.dto.request.GroupRequest;
import lms.dto.response.AllGroupResponse;
import lms.dto.response.GroupResponse;
import lms.dto.response.SimpleResponse;
import lms.entities.Group;
import lms.entities.Student;
import lms.entities.Trash;
import lms.enums.Type;
import lms.exceptions.AlreadyExistsException;
import lms.repository.GroupRepository;
import lms.repository.TrashRepository;
import lms.service.GroupService;
import lms.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional
@RequiredArgsConstructor
public class GroupServiceImpl implements GroupService {
    private final GroupRepository groupRepository;
    private final TrashRepository trashRepository;
    private final StudentService studentService;

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
    public SimpleResponse update(long groupId, GroupRequest groupRequest) {
        Group updatedGroup = getById(groupId);

        if (!updatedGroup.getTitle().equals(groupRequest.title())) {
            boolean exists = groupRepository.existsByTitle(groupRequest.title());
            if (exists)
                throw new AlreadyExistsException("Группа с названием " + groupRequest.title() + " уже существует!");
        }
        updatedGroup.setTitle(groupRequest.title());
        updatedGroup.setDescription(groupRequest.description());
        updatedGroup.setImage(groupRequest.image());
        updatedGroup.setDateOfEnd(groupRequest.dateOfEnd());

        return SimpleResponse.builder()
                .httpStatus(HttpStatus.OK)
                .message("Успешно обнавлено!")
                .build();
    }

    @Override
    public AllGroupResponse findAllGroup(int size, int page) {
        if (page < 1 && size < 1) throw new IllegalArgumentException("Индекс страницы не должен быть меньше нуля");
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("id"));
        Page<GroupResponse> allGroup = groupRepository.findAllGroup(pageable);

        return AllGroupResponse.builder()
                .page(allGroup.getNumber() + 1)
                .size(allGroup.getNumberOfElements())
                .groupResponses(allGroup.getContent())
                .build();
    }

    @Override
    public SimpleResponse delete(long groupId) {
        Group group = getById(groupId);
        Trash trash = new Trash();
        trash.setName(group.getTitle());
        trash.setType(Type.GROUP);
        trash.setDateOfDelete(ZonedDateTime.now());
        trash.setGroup(group);
        group.setTrash(trash);

        for (Student student : group.getStudents()) {
            studentService.delete(student.getId());
        }
        trashRepository.save(trash);
        return SimpleResponse.builder()
                .httpStatus(HttpStatus.OK)
                .message("Группа успешно добавлено в корзину!")
                .build();
    }

    @Override
    public GroupResponse getGroup(Long groupId) {
        Group group = getById(groupId);
        return GroupResponse.builder()
                .id(groupId)
                .title(group.getTitle())
                .description(group.getDescription())
                .image(group.getImage())
                .dateOfStart(group.getDateOfStart())
                .dateOfEnd(group.getDateOfEnd())
                .build();
    }

    private Group getById(long id) {
        return groupRepository.findById(id).orElseThrow(() -> new NoSuchElementException("Группа не найдена"));
    }

}
