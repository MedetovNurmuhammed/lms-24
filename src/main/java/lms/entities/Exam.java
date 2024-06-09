package lms.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "exams")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Exam {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "exam_gen")
    @SequenceGenerator(name = "exam_gen", sequenceName = "exam_seq", allocationSize = 1)
    private long id;
    private String title;
    private LocalDate examDate;
    private LocalDate updatedAt;
    @ManyToOne(fetch =  FetchType.LAZY)
    private Course course;
    @OneToMany(mappedBy = "exam", cascade = CascadeType.ALL, orphanRemoval = true, fetch =  FetchType.LAZY)
    private List<ExamResult> examResults = new ArrayList<>();

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDate.now();
    }}

