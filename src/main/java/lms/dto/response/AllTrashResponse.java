package lms.dto.response;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AllTrashResponse {
    private int page;
    private int size;
    private List<TrashResponse> trashResponses = new ArrayList<>();
}
