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
import lms.enums.Type;
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
    @SequenceGenerator(name = "student_gen",sequenceName = "student_seq", allocationSize = 1,initialValue = 21)
    private Long id;
    @Enumerated(EnumType.STRING)
    private StudyFormat studyFormat;
    private LocalDate createdAt;
    private LocalDate updatedAt;
    private Type type;


    public Type getType() {
        return type = Type.STUDENT;
    }

    //********************************* User **********************************************
    @OneToOne(cascade = CascadeType.REMOVE,orphanRemoval = true)
    private User user;

    //********************************* Group *********************************************
    @ManyToOne(cascade = CascadeType.DETACH,optional = false)
    private Group group;

    //********************************* ResultTest ****************************************
    @OneToMany(mappedBy = "student", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<ResultTest> resultTests = new ArrayList<>();

    //********************************* AnswerTask *****************************************
    @OneToMany(mappedBy = "student", cascade = CascadeType.REMOVE,orphanRemoval = true)
    private List<AnswerTask> answerTasks = new ArrayList<>();

    //********************************* Notification ***************************************
    @OneToMany(mappedBy = "student",cascade = CascadeType.REMOVE,orphanRemoval = true)
    private List<Notification> notifications = new ArrayList<>();

    //********************************* Course *********************************************
    @OneToOne
    private Trash trash;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDate.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDate.now();
    }
}
