package lms.dto.response;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AllTrashResponse {
    private int page;
    private int size;
    private List<TrashResponse> trashResponses = new ArrayList<>();
}
