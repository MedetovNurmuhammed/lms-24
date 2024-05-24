package lms.dto;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

public record FindAllStudentsRequestParams(int page, int size, String search, String studyFormat, Long groupId) {
    public Pageable getPageable() {
        return PageRequest.of(page - 1, size);
    }
}