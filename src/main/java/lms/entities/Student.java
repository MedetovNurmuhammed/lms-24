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
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.CascadeType;
import lms.enums.StudyFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "students")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "student_gen")
    @SequenceGenerator(name = "student_seq",sequenceName = "student_seq", allocationSize = 1)
    private Long id;
    @Enumerated(EnumType.STRING)
    private StudyFormat studyFormat;
    private LocalDate createdAt;
    private LocalDate updatedAt;

    //********************************* User **********************************************
    @OneToOne(cascade = CascadeType.REMOVE)
    private User user;

    //********************************* Group *********************************************
    @ManyToOne(cascade = CascadeType.DETACH)
    private Group group;

    //********************************* ResultTest ****************************************
    @OneToMany(mappedBy = "student", cascade = CascadeType.REMOVE,orphanRemoval = true)
    private List<ResultTest> resultTests = new ArrayList<>();

    //********************************* AnswerTask *****************************************
    @OneToMany(mappedBy = "student", cascade = CascadeType.REMOVE,orphanRemoval = true)
    private List<AnswerTask> answerTasks = new ArrayList<>();

    //********************************* Notification ***************************************
    @OneToMany(mappedBy = "student",cascade = CascadeType.REMOVE,orphanRemoval = true)
    private List<Notification> notifications = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDate.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDate.now();
    }
}
