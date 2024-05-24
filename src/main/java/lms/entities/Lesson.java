package lms.entities;

import jakarta.persistence.*;
import lms.enums.Type;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.persistence.*;
import lombok.*;

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
    @SequenceGenerator(name = "lesson_gen", sequenceName = "lesson_seq", allocationSize = 1, initialValue = 21)
    private Long id;
    private String title;
    private LocalDate createdAt;
    private LocalDate updatedAt;
    @Enumerated(EnumType.STRING)
    private Type type;

    //********************************* Course ********************************************
    @ManyToOne(cascade = CascadeType.DETACH, fetch = FetchType.LAZY, optional = false)
    private Course course;

    //*************************************** Video ***************************************
    @OneToMany(cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Video> videos = new ArrayList<>();

    //*************************************** Presentation ********************************
    @OneToMany(cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Presentation> presentations = new ArrayList<>();

    //*************************************** Link *****************************************
    @OneToMany(mappedBy = "lesson", cascade = CascadeType.REMOVE, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Link> links = new ArrayList<>();

    //*************************************** Test *****************************************
    @OneToMany(cascade = CascadeType.REMOVE, mappedBy = "lesson", orphanRemoval = true)
    private List<Test> tests;

    //*************************************** Task *****************************************
    @OneToMany(cascade = CascadeType.REMOVE, mappedBy = "lesson",fetch = FetchType.LAZY,orphanRemoval = true)
    private List<Task> tasks = new ArrayList<>();

    @OneToOne(fetch = FetchType.LAZY)
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
