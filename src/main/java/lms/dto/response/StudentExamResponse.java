package lms.dto.response;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;
@Data
public class StudentExamResponse {
    private Long studentId;
    private String studentName;
    private List<ExamInfo> exams;

    @Data
    public static class ExamInfo {
        private Long examId;
        private String examTitle;
        private LocalDate examDate;
        private int point;

    }
}
