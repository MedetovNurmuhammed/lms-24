package lms.entities;

import jakarta.persistence.*;
import lms.enums.StudyFormat;
import lms.enums.Type;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Cascade;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.LinkedHashMap;

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
    @OneToOne(cascade = CascadeType.ALL,orphanRemoval = true, fetch =  FetchType.LAZY)
    private User user;

    //********************************* Group *********************************************
    @ManyToOne(cascade = CascadeType.DETACH, optional = false, fetch = FetchType.LAZY)
    private Group group;

    //********************************* ResultTest ****************************************
    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, orphanRemoval = true, fetch =  FetchType.LAZY)
    private List<ResultTest> resultTests = new ArrayList<>();

    //********************************* AnswerTask *****************************************
    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL,orphanRemoval = true, fetch =  FetchType.LAZY)
    private List<AnswerTask> answerTasks = new ArrayList<>();

    //********************************* Notification ***************************************
    @ElementCollection(fetch = FetchType.LAZY)
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    @MapKeyJoinColumn(name = "notification_id")
    private Map< Notification, Boolean> notificationStates = new HashMap<>();

    //********************************* Announcement ***************************************
    @ElementCollection(fetch = FetchType.LAZY)
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    private Map<Announcement,Boolean> announcements = new LinkedHashMap<>();

    //********************************* Trash *********************************************
    @OneToOne(fetch =  FetchType.LAZY, cascade = CascadeType.ALL)
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
