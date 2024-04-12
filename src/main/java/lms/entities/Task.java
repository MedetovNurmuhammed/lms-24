package lms.entities;

import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.CascadeType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tasks")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "task_gen")
    @SequenceGenerator(name = "task_seq",sequenceName = "task_seq", allocationSize = 1)
    private Long id;
    private String title;
    private String description;
    private String file;
    private String image;
    private String code;
    private LocalDate deadline;
    private LocalDate createdAt;
    private LocalDate updatedAt;

    //*************************************** Instructor ***********************************
    @ManyToOne(cascade = CascadeType.DETACH)
    private Instructor instructor;

    //*************************************** AnswerTask ***********************************
    @OneToMany(cascade = CascadeType.REMOVE)
    private List<AnswerTask> answerTasks = new ArrayList<>();

    //*************************************** Link ******************************************
    @OneToMany(cascade = CascadeType.REMOVE)
    private List<Link> links = new ArrayList<>();

    //*************************************** Lesson ****************************************
    @ManyToOne(cascade = CascadeType.DETACH)
    private Lesson lesson;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDate.now();
    }

   @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDate.now();
   }
}