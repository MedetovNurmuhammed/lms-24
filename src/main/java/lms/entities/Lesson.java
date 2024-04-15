package lms.entities;

import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
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
@Table(name = "lessons")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Lesson {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "lesson_gen")
    @SequenceGenerator(name = "lesson_seq",sequenceName = "lesson_seq", allocationSize = 1)
    private Long id;
    private String title;
    private LocalDate createdAt;
    private LocalDate updatedAt;

    //********************************* Course ********************************************
    @ManyToOne(cascade = CascadeType.DETACH,fetch = FetchType.EAGER,optional = false)
    private Course course;

    //*************************************** Video ***************************************
    @OneToMany(cascade = CascadeType.REMOVE,orphanRemoval = true)
    private List<Video> videos = new ArrayList<>();

    //*************************************** Presentation ********************************
    @OneToMany(cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Presentation> presentations = new ArrayList<>();

    //*************************************** Link *****************************************
    @OneToMany(mappedBy = "lesson", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Link> links = new ArrayList<>();

    //*************************************** Test *****************************************
    @OneToOne(cascade = CascadeType.REMOVE, mappedBy = "lesson",orphanRemoval = true)
    private Test test ;

    //*************************************** Task *****************************************
    @OneToMany(cascade = CascadeType.REMOVE, mappedBy = "lesson",orphanRemoval = true)
    private List<Task> tasks = new ArrayList<>();


    @PrePersist
    protected void onCreate() {
        createdAt = LocalDate.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDate.now();
    }
}
