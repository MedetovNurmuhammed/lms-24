package lms.dto.response;

import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Builder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class FindNotificationTaskResponse {
    private Long taskId;
    private String title;
    private String description;
    private String file;
    private String image;
    private String code;
    private LocalDateTime deadline;
    private List<String> links = new ArrayList<>();

}
