package lms.dto.request;

import jakarta.validation.constraints.Min;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ExamPointRequest {
   @Min(value = 0)
   private int point;
}
