package lms.service.impl;

import lms.dto.response.AllTrashResponse;
import lms.dto.response.SimpleResponse;
import lms.dto.response.TrashResponse;
import lms.entities.Trash;
import lms.entities.User;
import lms.enums.Role;
import lms.repository.CourseRepository;
import lms.repository.StudentRepository;
import lms.repository.TrashRepository;
import lms.repository.UserRepository;
import lms.service.TrashService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TrashServiceImpl implements TrashService {

    private final TrashRepository trashRepository;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final StudentRepository studentRepository;
    private final TrashDeleteService trashDeleteService;

    @Override
    public AllTrashResponse findAll(int page, int size) {
        User authUser = userRepository.getByEmail(SecurityContextHolder.getContext().getAuthentication().getName());
        Pageable pageable = PageRequest.of(page - 1, size);
        if (authUser.getRole().equals(Role.ADMIN)) {
            Page<TrashResponse> allTrash = trashRepository.findAllTrash(pageable);
            return AllTrashResponse.builder()
                    .page(allTrash.getNumber() + 1)
                    .size(allTrash.getNumberOfElements())
                    .trashResponses(allTrash.getContent())
                    .build();
        }
        Page<TrashResponse> instructorTrash = trashRepository.findAllTrashByInstructorId(authUser.getId(), pageable);
        return AllTrashResponse.builder()
                .page(instructorTrash.getNumber() + 1)
                .size(instructorTrash.getNumberOfElements())
                .trashResponses(instructorTrash.getContent())
                .build();
    }


    @Override
    public SimpleResponse restoreData(Long trashId) {
        Trash trash = trashRepository.findByIdOrThrow(trashId);
        return null;
    }

    @Override
    public SimpleResponse delete(Long trashID) {
        Trash trash = trashRepository.findByIdOrThrow(trashID);
        deleteTrash(trash);
        return SimpleResponse.builder()
                .httpStatus(HttpStatus.OK)
                .message("Trash with id: %d deleted".formatted(trash.getId()))
                .build();
    }

    private void deleteTrash(Trash trash) {
        switch (trash.getType()) {
            case COURSE -> {
                courseRepository.delete(trash.getCourse());
                trashRepository.delete(trash);
            }
            case STUDENT -> {
                System.err.println("trash.getId() = " + trash.getId());
                trashDeleteService.deleteStudent(trash.getStudent());
                trashRepository.delete(trash);
            }
        }
    }

}


