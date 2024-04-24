package lms.entities;

import jakarta.persistence.*;
import lms.enums.StudyFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    //********************************* Announcement ***************************************
    @ElementCollection
    private Map<Announcement,Boolean> announcements = new HashMap<>();

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDate.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDate.now();
    }
}
