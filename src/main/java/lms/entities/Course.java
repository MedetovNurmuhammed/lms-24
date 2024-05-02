package lms.entities;

import jakarta.persistence.*;
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
@Table(name = "courses")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "course_gen")
    @SequenceGenerator(name = "course_gen",sequenceName = "course_seq", allocationSize = 1,initialValue = 21)
    private Long id;
    private String title;
    private String description;
    private String image;
    private LocalDate dateOfStart;
    private LocalDate dateOfEnd;
    private Type type;

    public Type getType() {
        return type = Type.COURSE;
    }

    //*************************************** Instructor *************************************
    @ManyToMany(mappedBy = "courses",cascade = CascadeType.DETACH,fetch = FetchType.LAZY)
    private List<Instructor> instructors = new ArrayList<>();

    //*************************************** Group ******************************************
    @ManyToMany(cascade = CascadeType.DETACH,fetch = FetchType.LAZY)
    private List<Group> groups = new ArrayList<>();

    //*************************************** Lesson ******************************************
    @OneToMany(mappedBy = "course",cascade = CascadeType.REMOVE,fetch = FetchType.LAZY,orphanRemoval = true)
    private List<Lesson> lessons = new ArrayList<>();

    //*************************************** Trash *******************************************
    @OneToOne()
    private Trash trash;

    @PrePersist
    protected void onCreate() {
        dateOfStart = LocalDate.now();
    }
}
