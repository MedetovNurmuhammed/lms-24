package lms.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Setter
@Getter
public class FindAllStudentsResponse {
    private int page;
    private int size;
    private AllStudentsResponse allStudentsResponse;
}
