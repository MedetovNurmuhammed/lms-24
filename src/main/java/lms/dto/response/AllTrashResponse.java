package lms.dto.response;


import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AllTrashResponse {
    public List<TrashResponse> trashResponses = new ArrayList<>();
}
