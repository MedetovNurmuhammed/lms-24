package lms.entities;


import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.GenerationType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.CascadeType;
import jakarta.persistence.PrePersist;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;

@Entity
@Table(name = "notifications")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "notification_gen")
    @SequenceGenerator(name = "notification_gen",sequenceName = "notification_seq", allocationSize = 1,initialValue = 21)
    private Long id;
    private String title;
    private String description;
    private LocalDate createdAt;

    //*************************************** Task ******************************************
    @ManyToOne(cascade = CascadeType.DETACH)
    private Task task;

    //*************************************** ResultTask *************************************
    @ManyToOne(cascade = CascadeType.DETACH)
    private AnswerTask answerTask;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDate.now();
    }
}
