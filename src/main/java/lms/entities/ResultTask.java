package lms.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;

@Entity
@Table(name = "result_tasks")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResultTask {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "res_task_gen")
    @SequenceGenerator(name = "res_task_seq",sequenceName = "res_task_seq", allocationSize = 1)
    private Long id;
    private int point;
    private String comment;
    private LocalDate createdAt;
    private LocalDate updatedAt;

    //*************************************** AnswerTask ******************************************
    @OneToOne(cascade = CascadeType.DETACH)
    private AnswerTask answerTask;

    //*************************************** Notification ****************************************
    @OneToOne(mappedBy = "resultTask", cascade = CascadeType.REMOVE,orphanRemoval = true)
    private Notification notification;

    //*************************************** Instructor ******************************************
    @ManyToOne(cascade = CascadeType.DETACH,fetch = FetchType.LAZY)
    private Instructor instructor;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDate.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDate.now();
    }
}