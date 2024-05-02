package lms.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
    @SequenceGenerator(name = "task_gen",sequenceName = "task_seq", allocationSize = 1,initialValue = 21)
    private Long id;
    private String title;
    private String description;
    private String file;
    private String image;
    private String code;
    @ElementCollection
    private List<String> links = new ArrayList<>();
    private LocalDateTime deadline;
    private LocalDate createdAt;
    private LocalDate updatedAt;

    //*************************************** Instructor ***********************************
    @ManyToOne(cascade = CascadeType.DETACH, fetch = FetchType.LAZY)
    private Instructor instructor;

    //*************************************** AnswerTask ***********************************
    @OneToMany(mappedBy = "task", cascade = CascadeType.REMOVE,orphanRemoval = true)
    private List<AnswerTask> answerTasks = new ArrayList<>();

    //*************************************** Lesson ****************************************
    @ManyToOne(cascade = CascadeType.DETACH,fetch = FetchType.LAZY)
    private Lesson lesson;

    @OneToOne(cascade = CascadeType.DETACH,fetch = FetchType.LAZY)
    private Trash trash;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDate.now();
    }

   @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDate.now();
   }
}
