package lms.dto.response;

import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class TaskResponse{
       private Long id;
       private String title;
       private String description;
       private String file;
       private String image;
       private String code;
       private LocalDateTime deadline;
       private List<String> links = new ArrayList<>();
}
