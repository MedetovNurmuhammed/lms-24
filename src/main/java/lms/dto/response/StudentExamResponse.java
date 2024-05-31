package lms.dto.response;

import lombok.Data;

import java.util.List;
@Data
public class StudentExamResponse {
    private Long studentId;
    private String studentName;
    private List<ExamInfo> exams;

    @Data
    public static class ExamInfo {
        private String examTitle;
        private int point;
    }
}
