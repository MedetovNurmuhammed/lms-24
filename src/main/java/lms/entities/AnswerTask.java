package lms.entities;

import jakarta.persistence.*;
import lms.enums.TaskAnswerStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;

@Entity
@Table(name = "answer_tasks")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnswerTask{
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "answer_task_gen")
    @SequenceGenerator(name = "answer_task_seq",sequenceName = "answer_task_seq", allocationSize = 1)
    private long id;
    private String text;
    private String image;
    private String comment;
    @Enumerated(EnumType.STRING)
    private TaskAnswerStatus taskAnswerStatus;
    private LocalDate dateOfSend;
    private LocalDate updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    private Trash trash;

    //*************************************** Student **************************************
    @ManyToOne(cascade = CascadeType.DETACH, fetch = FetchType.LAZY)
    private Student student;

    //*************************************** Task *****************************************
    @ManyToOne(cascade = CascadeType.DETACH,fetch = FetchType.LAZY)
    private Task task;

    //*************************************** Link *****************************************
    @OneToOne(cascade = CascadeType.REMOVE)
    private Link link;

    //*************************************** ResultTask ************************************
    @OneToOne(cascade = CascadeType.REMOVE)
    private ResultTask resultTask;

    @PrePersist
    protected void onCreate() {
        dateOfSend = LocalDate.now();
    }

    @PreUpdate
    protected void onUpdate() {
        dateOfSend = LocalDate.now();
    }
}
