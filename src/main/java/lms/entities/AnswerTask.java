package lms.entities;

import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Table;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.CascadeType;
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
public class AnswerTask {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "answer_gen")
    @SequenceGenerator(name = "answer_seq",sequenceName = "answer_seq", allocationSize = 1)
    private Long id;
    private String text;
    private String image;
    private String comment;
    @Enumerated(EnumType.STRING)
    private TaskAnswerStatus taskAnswerStatus;
    private LocalDate dateOfSend;
    private LocalDate updatedAt;

    //*************************************** Student **************************************
    @ManyToOne(cascade = CascadeType.DETACH)
    private Student student;

    //*************************************** Task *****************************************
    @OneToOne(cascade = CascadeType.DETACH)
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
