package lms.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.OneToMany;
import jakarta.persistence.EnumType;
import jakarta.persistence.PrePersist;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.CascadeType;
import jakarta.persistence.FetchType;
import lms.enums.TaskAnswerStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "answer_tasks")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnswerTask {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "answer_task_gen")
    @SequenceGenerator(name = "answer_task_gen", sequenceName = "answer_task_seq", allocationSize = 1, initialValue = 21)
    private long id;
    private String text;
    private String image;
    private String file;
    @Enumerated(EnumType.STRING)
    private TaskAnswerStatus taskAnswerStatus;
    private int point = 0;
    @OneToMany(mappedBy = "answerTask", cascade = CascadeType.REMOVE, orphanRemoval = true, fetch =  FetchType.LAZY)
    private List<Comment> comments = new ArrayList<>();
    private LocalDateTime dateOfSend;
    private LocalDateTime updatedAt;

    //*************************************** Student **************************************
    @ManyToOne(cascade = CascadeType.DETACH, fetch = FetchType.LAZY)
    private Student student;

    //*************************************** Task *****************************************
    @ManyToOne(cascade = CascadeType.DETACH, fetch = FetchType.LAZY)
    private Task task;

    @PrePersist
    protected void onCreate() {
        dateOfSend = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        dateOfSend = LocalDateTime.now();
    }
}
