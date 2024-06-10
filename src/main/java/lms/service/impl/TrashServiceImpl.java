package lms.service.impl;

import lms.dto.response.AllTrashResponse;
import lms.dto.response.SimpleResponse;
import lms.dto.response.TrashResponse;
import lms.entities.Trash;
import lms.entities.User;
import lms.enums.Role;
import lms.repository.StudentRepository;
import lms.repository.TrashRepository;
import lms.repository.UserRepository;
import lms.service.TrashService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TrashServiceImpl implements TrashService {

    private final TrashRepository trashRepository;
    private final UserRepository userRepository;
    private final TrashDeleteService trashDeleteService;
    private final StudentRepository studentRepository;

    @Override
    public AllTrashResponse findAll(int page, int size) {
        User authUser = userRepository.getByEmail(SecurityContextHolder.getContext().getAuthentication().getName());
        if (authUser.getRole().equals(Role.ADMIN)) {
            Page<TrashResponse> instructorTrash =
                    trashRepository.findAllTrash(PageRequest.of(page - 1, size));
            return AllTrashResponse.builder()
                    .page(instructorTrash.getNumber() + 1)
                    .size(instructorTrash.getNumberOfElements())
                    .trashResponses(instructorTrash.getContent())
                    .build();
        }
        Page<TrashResponse> instructorTrash =
                trashRepository.findAllTrashByAuthId(authUser.getId(), PageRequest.of(page - 1, size));
        return AllTrashResponse.builder()
                .page(instructorTrash.getNumber() + 1)
                .size(instructorTrash.getNumberOfElements())
                .trashResponses(instructorTrash.getContent())
                .build();
    }


    @Override
    public SimpleResponse restoreData(Long trashId) {
        Trash trash = trashRepository.findByIdOrThrow(trashId);
        deleteTrash(trash, true);
        trashRepository.deleteTrash(trashId);
      return SimpleResponse.builder().httpStatus(HttpStatus.OK).message("Данные успешно восстановлены").build();
    }

    @Override
    public SimpleResponse delete(Long trashID) {
        Trash trash = trashRepository.findByIdOrThrow(trashID);
        deleteTrash(trash, false);
        return SimpleResponse.builder()
                .httpStatus(HttpStatus.OK)
                .message("Trash with id: %d deleted".formatted(trash.getId()))
                .build();
    }

    private void deleteTrash(Trash trash, boolean isRestored) {
        switch (trash.getType()) {
            case COURSE -> trashDeleteService.deleteGroup(trash);
            case STUDENT -> {
                if (isRestored) {
                    studentRepository.getStudentByTrashId(trash.getId())
                    trashRepository.delete(trash);
                }
                            trashDeleteService.deleteStudent(trash);
                }
            }
        }
    }

}


