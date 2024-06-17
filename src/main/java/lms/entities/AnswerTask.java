package lms.entities;

import jakarta.persistence.*;
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
public class  AnswerTask {
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

    // added notification list
    @OneToMany(cascade = {CascadeType.DETACH, CascadeType.REMOVE}, mappedBy = "answerTask")
    private List<Notification> notifications;

    //*************************************** Student ************************************** remove
    @ManyToOne(cascade = CascadeType.DETACH, fetch = FetchType.LAZY)
    private Student student;

    //*************************************** Task *****************************************
    @ManyToOne(cascade = CascadeType.DETACH, fetch = FetchType.LAZY)
    private Task task;

    //*************************************** Link *****************************************
    @OneToOne(cascade = CascadeType.REMOVE,orphanRemoval = true)
    private Link link;

    @PrePersist
    protected void onCreate() {
        dateOfSend = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        dateOfSend = LocalDateTime.now();
    }
}
