package lms.service;

import lms.dto.response.AllTrashResponse;
import lms.dto.response.SimpleResponse;
import org.springframework.stereotype.Service;

@Service
public interface TrashService {

    AllTrashResponse findAll(int page, int size);

    SimpleResponse delete(Long trashId);

    SimpleResponse returnToBase(Long trashId);

    AllTrashResponse findAllInstructorTrash(int page, int size);

    SimpleResponse returnInstructorTrashToBase(Long trashId);

    SimpleResponse deleteInstructorTrash(Long trashId);
}
