package lms.dto.response;

import lombok.*;

import java.util.List;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LessonRatingResponse {
    private Long id;
    private String title;
    private List<TaskRatingResponse> taskRatingResponses;

}
