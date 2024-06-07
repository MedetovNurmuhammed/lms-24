package lms.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "exam_results")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExamResult {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "exam_result_gen")
    @SequenceGenerator(name = "exam_result_gen", sequenceName = "exam_result_seq", allocationSize = 1)
    private Long id;

    @ManyToOne(optional = false, fetch =  FetchType.LAZY)
    private Student student;

    @ManyToOne(optional = false, fetch =  FetchType.LAZY)
    private Exam exam;

    private int point;
}
