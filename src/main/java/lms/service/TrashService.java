package lms.service;

import lms.dto.response.AllTrashResponse;
import lms.dto.response.SimpleResponse;
import lms.dto.response.TrashResponse;
import org.springframework.stereotype.Service;

@Service
public interface TrashService {

    AllTrashResponse findAll(int page, int size);

    SimpleResponse delete(Long trashId);

    SimpleResponse returnToBase(Long trashId);

    TrashResponse find(Long trashId);
}
