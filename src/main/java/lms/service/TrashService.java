package lms.service;

import lms.dto.response.AllTrashResponse;
import lms.dto.response.SimpleResponse;
import org.springframework.stereotype.Service;

@Service
public interface TrashService {

    AllTrashResponse findAll(int page, int size);

    SimpleResponse delete(Long trashId);

    SimpleResponse returnToBase(Long trashId);

    SimpleResponse deleteInstructorTrash(Long trashId);

    SimpleResponse returnFromInstructorTrashToBase(Long trashId);
}
