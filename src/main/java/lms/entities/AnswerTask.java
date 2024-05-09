package lms.entities;

import jakarta.persistence.*;
import lms.enums.TaskAnswerStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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
    @SequenceGenerator(name = "answer_task_gen",sequenceName = "answer_task_seq", allocationSize = 1, initialValue = 21)
    private long id;
    private String text;
    private String image;
    private String file;
    @Enumerated(EnumType.STRING)
    private TaskAnswerStatus taskAnswerStatus;
    private int point = 0;
    @OneToMany(mappedBy = "answerTask", cascade = CascadeType.REMOVE,orphanRemoval = true)
    private List<Comment> comment = new ArrayList<>();
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

    @PrePersist
    protected void onCreate() {
        dateOfSend = LocalDate.now();
    }

    @PreUpdate
    protected void onUpdate() {
        dateOfSend = LocalDate.now();
    }
}
