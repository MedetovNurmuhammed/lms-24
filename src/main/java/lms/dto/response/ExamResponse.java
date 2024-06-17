package lms.dto.response;

import lombok.Builder;

import java.time.LocalDate;

@Builder
public record ExamResponse (
        String examTitle,
        LocalDate examDate){
}
