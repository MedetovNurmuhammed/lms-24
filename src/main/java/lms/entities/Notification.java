package lms.entities;

import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.CascadeType;
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
    private Boolean isView;
    private LocalDate createdAt;

    //*************************************** Instructor ************************************
    @ManyToOne(cascade = CascadeType.DETACH)
    private Instructor instructor;

    //*************************************** Student ***************************************
    @ManyToOne(cascade = CascadeType.DETACH, optional = false)
    private Student student;

    //*************************************** Task ******************************************
    @OneToOne(cascade = CascadeType.DETACH, optional = false)
    private Task task;

    //*************************************** ResultTask *************************************
    @OneToOne(cascade = CascadeType.DETACH)
    private ResultTask resultTask;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDate.now();
    }
}
